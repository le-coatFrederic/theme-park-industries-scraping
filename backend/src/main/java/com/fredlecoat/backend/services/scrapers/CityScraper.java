package com.fredlecoat.backend.services.scrapers;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.values.CityDifficulty;

@Component
public class CityScraper {

    private static final String WORLD_MAP_PAGE = "game/carte_du_monde.php";
    private static final int COUNTRY_LOAD_DELAY_MS = 500;
    private static final int CITY_LOAD_DELAY_MS = 700;

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CityService cityService;

    public void scrapeAllCities() {
        System.out.println("DEBUT SCRAPING DES VILLES");

        try {
            WebDriver driver = loginService.getDriver();
            driver.get(accessConfig.getUrl() + WORLD_MAP_PAGE);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            iterateCountriesAndCities(driver, wait);

            System.out.println("SCRAPING DES VILLES TERMINE");

        } catch (Exception e) {
            System.err.println("ERREUR SCRAPING VILLES: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void iterateCountriesAndCities(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        WebElement countrySelectElement = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("countrySelect"))
        );

        Select countrySelect = new Select(countrySelectElement);
        int countryCount = countrySelect.getOptions().size();
        System.out.println("NOMBRE DE PAYS: " + countryCount);

        for (int countryIndex = 0; countryIndex < countryCount; countryIndex++) {
            processCountry(driver, countryIndex);
        }
    }

    private void processCountry(WebDriver driver, int countryIndex) throws InterruptedException {
        Select countrySelect = new Select(driver.findElement(By.id("countrySelect")));
        String countryName = countrySelect.getOptions().get(countryIndex).getText();

        System.out.println("PAYS: " + countryName);
        countrySelect.selectByIndex(countryIndex);
        Thread.sleep(COUNTRY_LOAD_DELAY_MS);

        processCitiesInCountry(driver);
    }

    private void processCitiesInCountry(WebDriver driver) throws InterruptedException {
        Select citySelect = new Select(driver.findElement(By.id("citySelect")));
        int cityCount = citySelect.getOptions().size();
        System.out.println("  NOMBRE DE VILLES: " + cityCount);

        for (int cityIndex = 0; cityIndex < cityCount; cityIndex++) {
            processCity(driver, cityIndex);
        }
    }

    private void processCity(WebDriver driver, int cityIndex) throws InterruptedException {
        Select citySelect = new Select(driver.findElement(By.id("citySelect")));
        List<WebElement> cityOptions = citySelect.getOptions();

        if (cityIndex >= cityOptions.size()) {
            return;
        }

        String cityFullName = cityOptions.get(cityIndex).getText();
        System.out.println("    VILLE: " + cityFullName);

        citySelect.selectByIndex(cityIndex);
        Thread.sleep(CITY_LOAD_DELAY_MS);

        extractAndSaveCity(driver);
    }

    private void extractAndSaveCity(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> cityDetails = (Map<String, Object>) js.executeScript(buildCityExtractionScript());

            if (cityDetails != null) {
                CityEntity city = createCityFromData(cityDetails);
                cityService.create(city);
                System.out.println("      VILLE SAUVEGARDEE: " + city.getName());
            }

        } catch (Exception e) {
            System.err.println("      ERREUR EXTRACTION VILLE: " + e.getMessage());
        }
    }

    private CityEntity createCityFromData(Map<String, Object> data) {
        String cityName = data.get("cityName").toString();
        String country = data.get("country").toString();
        CityDifficulty difficulty = translateDifficulty(data.get("difficulty").toString());

        Long population = parseNumber(data.get("population").toString());
        Long availableSurface = parseNumber(data.get("surface").toString());
        Long totalSurface = calculateTotalSurface(availableSurface, data.get("fillRate").toString());

        int maxHeight = parseInteger(data.get("maxHeight").toString());
        int priceByMeter = parseInteger(data.get("pricePerM2").toString());

        String capacityStr = data.get("capacity").toString();
        int[] parkData = parseCapacity(capacityStr);

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
            case "Facile" -> CityDifficulty.EASY;
            case "Modéré" -> CityDifficulty.MEDIUM;
            case "Difficile" -> CityDifficulty.HARD;
            default -> throw new IllegalArgumentException("Difficulté inconnue: " + difficulty);
        };
    }

    private Long parseNumber(String numberStr) {
        String cleaned = numberStr.replaceAll("[^0-9]", "");
        return cleaned.isEmpty() ? null : Long.parseLong(cleaned);
    }

    private int parseInteger(String str) {
        return Integer.parseInt(str.replaceAll("[^0-9]", ""));
    }

    private String buildCityExtractionScript() {
        return """
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
    }
}
