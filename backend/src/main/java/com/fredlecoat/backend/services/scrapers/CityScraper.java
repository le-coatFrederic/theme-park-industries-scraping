package com.fredlecoat.backend.services.scrapers;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.configuration.ScrapingConfig;
import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.utils.ScrapingParser;
import com.fredlecoat.backend.values.CityDifficulty;

/**
 * Scraper for extracting city data from the world map page.
 *
 * Responsibilities:
 * - Navigate through countries and cities on the world map
 * - Extract city details (population, surface, difficulty, etc.)
 * - Extract parks associated with each city
 * - Delegate persistence to appropriate services
 */
@Component
public class CityScraper extends BaseScraper {

    private final CityService cityService;
    private final ParkService parkService;
    private final PlayerService playerService;

    @Autowired
    public CityScraper(CityService cityService, ParkService parkService, PlayerService playerService) {
        this.cityService = cityService;
        this.parkService = parkService;
        this.playerService = playerService;
    }

    @Override
    public void scrape() {
        scrapeAllCities();
    }

    public void scrapeAllCities() {
        System.out.println("DEBUT SCRAPING DES VILLES");

        try {
            navigateTo(ScrapingConfig.WORLD_MAP_PAGE);
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            iterateCountriesAndCities(wait);
            System.out.println("SCRAPING DES VILLES TERMINE");

        } catch (Exception e) {
            System.err.println("ERREUR SCRAPING VILLES: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void iterateCountriesAndCities(WebDriverWait wait) throws InterruptedException {
        WebElement countrySelectElement = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id(ScrapingConfig.Selectors.COUNTRY_SELECT))
        );

        Select countrySelect = new Select(countrySelectElement);
        int countryCount = countrySelect.getOptions().size();
        System.out.println("NOMBRE DE PAYS: " + countryCount);

        for (int countryIndex = 0; countryIndex < countryCount; countryIndex++) {
            processCountry(countryIndex);
        }
    }

    private void processCountry(int countryIndex) throws InterruptedException {
        WebDriver driver = getDriver();
        Select countrySelect = new Select(driver.findElement(By.id(ScrapingConfig.Selectors.COUNTRY_SELECT)));
        String countryName = countrySelect.getOptions().get(countryIndex).getText();

        System.out.println("PAYS: " + countryName);
        countrySelect.selectByIndex(countryIndex);
        sleep(ScrapingConfig.COUNTRY_LOAD_DELAY_MS);

        processCitiesInCountry();
    }

    private void processCitiesInCountry() throws InterruptedException {
        WebDriver driver = getDriver();
        Select citySelect = new Select(driver.findElement(By.id(ScrapingConfig.Selectors.CITY_SELECT)));
        int cityCount = citySelect.getOptions().size();
        System.out.println("  NOMBRE DE VILLES: " + cityCount);

        for (int cityIndex = 0; cityIndex < cityCount; cityIndex++) {
            processCity(cityIndex);
        }
    }

    private void processCity(int cityIndex) throws InterruptedException {
        WebDriver driver = getDriver();
        Select citySelect = new Select(driver.findElement(By.id(ScrapingConfig.Selectors.CITY_SELECT)));
        List<WebElement> cityOptions = citySelect.getOptions();

        if (cityIndex >= cityOptions.size()) {
            return;
        }

        String cityFullName = cityOptions.get(cityIndex).getText();
        System.out.println("    VILLE: " + cityFullName);

        citySelect.selectByIndex(cityIndex);
        sleep(ScrapingConfig.CITY_LOAD_DELAY_MS);

        extractAndSaveCity();
    }

    private void extractAndSaveCity() {
        try {
            Map<String, Object> cityDetails = executeScriptAsMap(CityExtractionScripts.CITY_DETAILS);

            if (cityDetails != null) {
                CityEntity city = createCityFromData(cityDetails);
                city = cityService.save(city);
                System.out.println("      VILLE SAUVEGARDEE: " + city.getName());

                extractAndUpdateParks(city);
            }

        } catch (Exception e) {
            System.err.println("      ERREUR EXTRACTION VILLE: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void extractAndUpdateParks(CityEntity city) {
        try {
            List<Map<String, String>> parksData = (List<Map<String, String>>) executeScript(CityExtractionScripts.PARKS_LIST);

            if (parksData == null || parksData.isEmpty()) {
                System.out.println("      Aucun parc trouvé pour cette ville");
                return;
            }

            System.out.println("      PARCS TROUVES: " + parksData.size());

            for (Map<String, String> parkData : parksData) {
                updateParkWithCityAndOwner(parkData, city);
            }

        } catch (Exception e) {
            System.err.println("      ERREUR EXTRACTION PARCS: " + e.getMessage());
        }
    }

    private void updateParkWithCityAndOwner(Map<String, String> parkData, CityEntity city) {
        String parkName = parkData.get("name");
        String creatorName = parkData.get("creator");

        if (parkName == null || parkName.isEmpty()) {
            return;
        }

        PlayerEntity owner = null;
        if (creatorName != null && !creatorName.isEmpty()) {
            owner = playerService.findOrCreate(creatorName);
        }

        parkService.updateOwnerAndCity(parkName, owner, city);
    }

    private CityEntity createCityFromData(Map<String, Object> data) {

        data.forEach((k,v) -> {
            System.out.println(k + " : " + v);
        });

        String cityName = data.get("cityName").toString();
        String country = data.get("country").toString();
        CityDifficulty difficulty = translateDifficulty(data.get("difficulty").toString());

        Long population = Integer.toUnsignedLong(ScrapingParser.parseInteger(data.get("population")));
        Long availableSurface = Integer.toUnsignedLong(ScrapingParser.parseSurface(data.get("surface")));
        Long totalSurface = calculateTotalSurface(availableSurface, data.get("fillRate").toString());

        int maxHeight = ScrapingParser.parseIntegerOrDefault(data.get("maxHeight"), 0);
        int priceByMeter = ScrapingParser.parseMoney(data.get("pricePerM2"));

        String capacityStr = data.get("capacity").toString();
        int[] parkData = parseCapacity(capacityStr);

        System.out.println(
            "City name : " + cityName +
            "\nDifficulty : " + difficulty +
            "\nCountry : " + country +
            "\nPopulation : " + population +
            "\nAvalaible Surface : " + availableSurface +
            "\nTotal Surface : " + totalSurface +
            "\nMax Height : " + maxHeight +
            "\nNumber of parks : " + parkData[0] +
            "\nTotal places for parks : " + parkData[1] +
            "\nPrice by meter : " + priceByMeter
        );

        return new CityEntity(
            cityName,
            difficulty,
            country,
            population,
            availableSurface,
            totalSurface,
            maxHeight,
            parkData[0],
            parkData[1],
            priceByMeter
        );
    }

    private Long calculateTotalSurface(Long availableSurface, String fillRateStr) {
        if (availableSurface == null) {
            return null;
        }
        double fillRate = Double.parseDouble(fillRateStr.replace("%", "").trim()) / 100;
        return Math.round(availableSurface / (1 - fillRate));
    }

    private int[] parseCapacity(String capacityStr) {
        String[] parts = capacityStr.split("/");
        int parkPopulation = Integer.parseInt(parts[0].trim());
        int parkCapacity = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
        return new int[]{parkPopulation, parkCapacity};
    }

    private CityDifficulty translateDifficulty(String difficulty) {
        return switch (difficulty) {
            case ScrapingConfig.DifficultyLabels.EASY -> CityDifficulty.EASY;
            case ScrapingConfig.DifficultyLabels.MEDIUM -> CityDifficulty.MEDIUM;
            case ScrapingConfig.DifficultyLabels.HARD -> CityDifficulty.HARD;
            default -> throw new IllegalArgumentException("Difficulté inconnue: " + difficulty);
        };
    }

    /**
     * JavaScript extraction scripts for city data.
     * Separated into inner class for better organization.
     */
    private static final class CityExtractionScripts {

        static final String CITY_DETAILS = """
            return (function() {
                const cityInfo = document.querySelector('.world-map-city-info');
                if (!cityInfo) return null;

                const titleElem = cityInfo.querySelector('.world-map-city-title');
                let cityName = titleElem.childNodes[0].textContent.trim();

                let country = null;
                const countryBadge = titleElem.querySelector('.world-map-badge');
                if (countryBadge) {
                    const badgeText = countryBadge.textContent.replace(/\\s+/g, ' ').trim();
                    country = badgeText.replace(/[^a-zA-ZÉèêàâôûüîïöäëñçÁÈÊÀÂÔÛÜÎÏÖÄËÑÇ\\s]/g, '').trim();
                }

                let difficulty = null;
                const diffBadge = titleElem.querySelector('.world-map-difficulty-badge');
                if (diffBadge) difficulty = diffBadge.textContent.replace(/\\s+/g, ' ').trim();

                let surface = null;
                let fillRate = null;
                const allDivs = Array.from(cityInfo.querySelectorAll('div'));
                for (let div of allDivs) {
                    if (div.textContent.includes('Surface disponible')) {
                        const strong = div.querySelector('strong');
                        if (strong) surface = strong.textContent.trim();
                    }
                    if (div.textContent.includes('Taux de remplissage')) {
                        const strong = div.querySelector('strong');
                        if (strong) fillRate = strong.textContent.trim();
                    }
                }

                const statDivs = Array.from(cityInfo.querySelectorAll('.world-map-city-stats > div'));
                let population = null, pricePerM2 = null, maxHeight = null, capacity = null;

                if (statDivs.length >= 6) {
                    population = statDivs[0]?.textContent?.split(':')[1]?.trim();
                    pricePerM2 = statDivs[2]?.textContent?.split(':')[1]?.trim();
                    maxHeight = statDivs[3]?.textContent?.split(':')[1]?.trim();
                    capacity = statDivs[5]?.textContent?.split(':')[1]?.trim();
                }

                return {
                    cityName,
                    country,
                    difficulty,
                    surface,
                    fillRate,
                    population,
                    pricePerM2,
                    maxHeight,
                    capacity
                };
            })();
            """;

        static final String PARKS_LIST = """
            return (function() {
                const parcsContainer = document.querySelector('.world-map-parcs-items');
                if (!parcsContainer) return [];

                const parcItems = parcsContainer.querySelectorAll('.world-map-parc-item');
                const parcs = [];

                for (const item of parcItems) {
                    const nameElem = item.querySelector('.world-map-parc-name');
                    const creatorElem = item.querySelector('.world-map-parc-creator strong');

                    parcs.push({
                        name: nameElem ? nameElem.textContent.trim() : null,
                        creator: creatorElem ? creatorElem.textContent.trim() : null
                    });
                }

                return parcs;
            })();
            """;

        private CityExtractionScripts() {}
    }
}
