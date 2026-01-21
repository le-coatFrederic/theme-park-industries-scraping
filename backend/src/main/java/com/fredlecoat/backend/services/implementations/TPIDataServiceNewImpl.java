package com.fredlecoat.backend.services.implementations;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.services.TPIDataService;
import com.fredlecoat.backend.values.CityDifficulty;
import com.fredlecoat.backend.values.RideType;

@Service
public class TPIDataServiceNewImpl implements TPIDataService {

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RideService rideService;

    @Autowired
    private CityService cityService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ParkService parkService;

    @Override
    public void getAllRidesData() {
        System.out.println("DEBUT GETALLRIDESDATA()");

        try {
            System.out.println("ACCES A LA PAGE");
            WebDriver driver = this.loginService.getDriver();
            driver.get(this.accessConfig.getUrl() + "game/park/attractions.php");
            System.out.println("ON A LA PAGE ATTRACTIONS");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            this.findRidesModal(driver, wait);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getAllCitiesData() {
        System.out.println("DEBUT GETALLCITIESDATA()");

        try {
            System.out.println("ACCES A LA PAGE");
            WebDriver driver = this.loginService.getDriver();
            driver.get(this.accessConfig.getUrl() + "game/carte_du_monde.php");
            System.out.println("ON A LA PAGE ATTRACTIONS");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            this.listAllCitiesInSelects(driver, wait);            
        } catch (Exception e) {
            e.printStackTrace();
        }          
    }

    @Override
    public void getAllParksData() {
        // TODO Auto-generated method stub
    }

    private void findRidesModal(WebDriver driver, WebDriverWait wait) {
        System.out.println("ON CHERCHE LE BOUTON POUR ACHETER DES ATTRACTIONS NEUVES");

        WebElement shopButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button#open-attraction-store-btn"))
        );
        
        System.out.println("ON A TROUVE LE BOUTON");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", shopButton);

        System.out.println("ON CHERCHE LA MODALE DES ATTRACTIONS");

        WebElement modal = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#attraction-store-modal")
            )
        );

        System.out.println("ON A TROUVE LA MODALE");

        //driver.switchTo().frame(modal);

        this.findAllBrandButtons(modal, wait);
    }

    private void findAllBrandButtons(WebElement modal, WebDriverWait wait) {

        WebDriver driver = ((org.openqa.selenium.remote.RemoteWebElement) modal).getWrappedDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        System.out.println("EXTRACTION DE TOUTES LES CARTES (VISIBLES ET CACHEES)");

        // JavaScript pour extraire TOUTES les cartes, y compris les cachées
        String script = "return Array.from(document.querySelectorAll('.attraction-card')).map(card => ({\n" +
            "  constructor: card.getAttribute('data-constructor'),\n" +
            "  type: card.getAttribute('data-type'),\n" +
            "  price: card.getAttribute('data-price'),\n" +
            "  hype: card.getAttribute('data-hype'),\n" +
            "  reliability: card.getAttribute('data-reliability'),\n" +
            "  name: card.querySelector('.attraction-card__title')?.textContent?.trim(),\n" +
            "  description: card.querySelector('.attraction-card__description strong:last-child')?.textContent?.trim(),\n" +
            "  manufacturer: card.querySelector('.attraction-card__manufacturer')?.textContent?.trim(),\n" +
            "}));";

        try {
            List<Object> cardsData = (List<Object>) js.executeScript(script);

            System.out.println("NOMBRE TOTAL DE CARTES TROUVEES : " + cardsData.size());

            for (Object cardObj : cardsData) {
                java.util.Map<String, Object> card = (java.util.Map<String, Object>) cardObj;

                RideType type = card.get("type").toString().equals("flatride") ? RideType.FLAT_RIDE : RideType.COASTER;
                String surface = card.get("description").toString();
                surface = surface.substring(0, surface.length() - 3);
                surface = surface.trim();
                surface = surface.replace(" ", "");

                RideEntity entity = new RideEntity(
                    type,
                    -1,
                    Integer.parseInt(card.get("hype").toString()),
                    card.get("name").toString(),
                    card.get("constructor").toString(),
                    Long.parseLong(card.get("price").toString()),
                    Long.valueOf(surface)
                );

                entity = this.rideService.create(entity);

                System.out.println(entity);


            }
        } catch (Exception e) {
            System.err.println("ERREUR LORS DE L'EXTRACTION DES DONNEES : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listAllCitiesInSelects(WebDriver driver, WebDriverWait wait) {
        System.out.println("DEBUT EXTRACTION DES VILLES PAR PAYS");

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Attendre que le select des pays soit disponible
            WebElement countrySelectElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("countrySelect"))
            );

            System.out.println("SELECT DES PAYS TROUVE");

            Select countrySelect = new Select(countrySelectElement);
            List<WebElement> countryOptions = countrySelect.getOptions();

            System.out.println("NOMBRE DE PAYS : " + countryOptions.size());

            // Parcourir chaque pays
            for (int i = 0; i < countryOptions.size(); i++) {
                // Récupérer à nouveau le select pour éviter les problèmes de stale element
                WebElement countrySelectRefresh = driver.findElement(By.id("countrySelect"));
                Select countrySelectRefreshed = new Select(countrySelectRefresh);
                String countryName = countrySelectRefreshed.getOptions().get(i).getText();

                System.out.println("SELECTION DU PAYS : " + countryName);

                // Sélectionner le pays par index
                countrySelectRefreshed.selectByIndex(i);

                // Attendre que le citySelect se mette à jour
                Thread.sleep(500); // Délai pour s'assurer que le DOM s'est mis à jour

                // Récupérer les villes du pays actuel
                WebElement citySelectElement = driver.findElement(By.id("citySelect"));
                Select citySelect = new Select(citySelectElement);
                List<WebElement> cityOptions = citySelect.getOptions();

                System.out.println("  NOMBRE DE VILLES : " + cityOptions.size());

                // Utiliser JavaScript pour extraire les données de toutes les villes
                String citiesScript = "return Array.from(document.querySelectorAll('#citySelect option')).map(option => ({\n" +
                    "  id: option.value,\n" +
                    "  name: option.textContent,\n" +
                    "}));";

                List<Object> citiesData = (List<Object>) js.executeScript(citiesScript);

                for (int j = 0; j < cityOptions.size(); j++) {
                    // Récupérer à nouveau le select pour éviter les problèmes de stale element
                    WebElement citySelectRefresh = driver.findElement(By.id("citySelect"));
                    Select citySelectRefreshed = new Select(citySelectRefresh);
                    List<WebElement> cityOptionsRefresh = citySelectRefresh.findElements(By.tagName("option"));

                    if (j < cityOptionsRefresh.size()) {
                        // Cliquer sur l'option de la ville
                        WebElement cityOption = cityOptionsRefresh.get(j);
                        String cityFullName = cityOption.getText();

                        System.out.println("    CLICK SUR LA VILLE : " + cityFullName);

                        citySelectRefreshed.selectByIndex(j);
                        Thread.sleep(700); // Attendre que le contenu se mette à jour

                        // Extraire les données du bloc world-map-city-info avec JavaScript
                        String cityDetailsScript = "return (function() {" +
                            "const cityInfo = document.querySelector('.world-map-city-info');" +
                            "if (!cityInfo) return null;" +
                            "const titleElem = cityInfo.querySelector('.world-map-city-title');" +
                            "let cityName = titleElem.childNodes[0].textContent.trim();" +
                            "let country = null;" +
                            "const countryBadge = titleElem.querySelector('.world-map-badge');" +
                            "if (countryBadge) {" +
                            "  const badgeText = countryBadge.textContent.replace(/\\s+/g, ' ').trim();" +
                            "  country = badgeText.replace(/[^a-zA-ZÉèêàâôûüîïöäëñçÁÈÊÀÂÔÛÜÎÏÖÄËÑÇ\\s]/g, '').trim();" +
                            "}" +
                            "let difficulty = null;" +
                            "const diffBadge = titleElem.querySelector('.world-map-difficulty-badge');" +
                            "if (diffBadge) difficulty = diffBadge.textContent.replace(/\\s+/g, ' ').trim();" +
                            "let surface = null; let fillRate = null;" +
                            "const allDivs = Array.from(cityInfo.querySelectorAll('div'));" +
                            "for (let div of allDivs) {" +
                            "  if (div.textContent.includes('Surface disponible')) {" +
                            "    const strong = div.querySelector('strong'); if (strong) surface = strong.textContent.trim();" +
                            "  }" +
                            "  if (div.textContent.includes('Taux de remplissage')) {" +
                            "    const strong = div.querySelector('strong'); if (strong) fillRate = strong.textContent.trim();" +
                            "  }" +
                            "}" +
                            "const statDivs = Array.from(cityInfo.querySelectorAll('.world-map-city-stats > div'));" +
                            "let population = null; let sunlight = null; let pricePerM2 = null; let maxHeight = null; let transport = null; let capacity = null;" +
                            "if (statDivs.length >= 6) {" +
                            "  population = statDivs[0]?.textContent?.split(':')[1]?.trim();" +
                            "  sunlight = statDivs[1]?.textContent?.split(':')[1]?.trim();" +
                            "  pricePerM2 = statDivs[2]?.textContent?.split(':')[1]?.trim();" +
                            "  maxHeight = statDivs[3]?.textContent?.split(':')[1]?.trim();" +
                            "  transport = statDivs[4]?.textContent?.split(':')[1]?.trim();" +
                            "  capacity = statDivs[5]?.textContent?.split(':')[1]?.trim();" +
                            "}" +
                            "const parks = [];" +
                            "const parksList = cityInfo.querySelector('.world-map-parcs-items');" +
                            "if (parksList) {" +
                            "  const parkItems = parksList.querySelectorAll('.world-map-parc-item');" +
                            "  for (let parkItem of parkItems) {" +
                            "    const parkName = parkItem.querySelector('.world-map-parc-name')?.textContent?.trim();" +
                            "    const creatorSpan = parkItem.querySelector('.world-map-parc-creator');" +
                            "    let creatorName = null;" +
                            "    if (creatorSpan) {" +
                            "      const strongElem = creatorSpan.querySelector('strong');" +
                            "      creatorName = strongElem?.textContent?.trim();" +
                            "    }" +
                            "    parks.push({ parkName: parkName, creator: creatorName });" +
                            "  }" +
                            "}" +
                            "return { cityName: cityName, country: country, difficulty: difficulty, surface: surface, fillRate: fillRate, population: population, sunlight: sunlight, pricePerM2: pricePerM2, maxHeight: maxHeight, transport: transport, capacity: capacity, parks: parks };" +
                            "})();";

                        try {
                            java.util.Map<String, Object> cityDetails = (java.util.Map<String, Object>) js.executeScript(cityDetailsScript);

                            if (cityDetails != null) {
                                String cityName = cityDetails.get("cityName").toString();
                                String countryStr = cityDetails.get("country").toString();
                                CityDifficulty difficulty = this.cityDifficultyTranslator(cityDetails.get("difficulty").toString());

                                // Parser population (ex: "250 000" -> 250000)
                                Long population = this.parseNumber(cityDetails.get("population").toString());

                                // Parser surface disponible (ex: "4 428 800 m²" -> 4428800)
                                Long availableSurface = this.parseNumber(cityDetails.get("surface").toString());

                                // Parser taux de remplissage (ex: "29.1%" -> 0.291)
                                String fillRateStr = cityDetails.get("fillRate").toString().replace("%", "").trim();
                                double fillRate = Double.parseDouble(fillRateStr) / 100;

                                // Calculer la surface totale: surface_totale = availableSurface / (1 - fillRate)
                                Long totalSurface = Math.round(availableSurface / (1 - fillRate));

                                // Parser hauteur max (ex: "90 m" -> 90)
                                int maxHeight = Integer.parseInt(cityDetails.get("maxHeight").toString().replaceAll("[^0-9]", ""));

                                // Parser prix par m² (ex: "550 €" -> 550)
                                int priceByMeter = Integer.parseInt(cityDetails.get("pricePerM2").toString().replaceAll("[^0-9]", ""));

                                // Parser capacité (ex: "34 / 104 parc(s)" -> parkPopulation=34, parkCapacity=104)
                                String capacityStr = cityDetails.get("capacity").toString();
                                String[] capacityParts = capacityStr.split("/");
                                int parkPopulation = Integer.parseInt(capacityParts[0].trim());
                                int parkCapacity = Integer.parseInt(capacityParts[1].replaceAll("[^0-9]", ""));

                                System.out.println("      Ville: " + cityName);
                                System.out.println("      Pays: " + countryStr);
                                System.out.println("      Difficulté: " + difficulty);
                                System.out.println("      Population: " + population);
                                System.out.println("      Surface disponible: " + availableSurface + " m²");
                                System.out.println("      Surface totale: " + totalSurface + " m²");
                                System.out.println("      Hauteur max: " + maxHeight + " m");
                                System.out.println("      Prix m²: " + priceByMeter + " €");
                                System.out.println("      Parcs présents / Capacité: " + parkPopulation + " / " + parkCapacity);

                                CityEntity city = new CityEntity(
                                    cityName,
                                    difficulty,
                                    countryStr,
                                    population,
                                    availableSurface,
                                    totalSurface,
                                    maxHeight,
                                    parkPopulation,
                                    parkCapacity,
                                    priceByMeter
                                );

                                city = this.cityService.create(city);
                                System.out.println("      VILLE SAUVEGARDEE");

                                // Récupérer et créer les parcs
                                List<Object> parksList = (List<Object>) cityDetails.get("parks");
                                if (parksList != null && !parksList.isEmpty()) {
                                    System.out.println("      NOMBRE DE PARCS : " + parksList.size());

                                    for (Object parkObj : parksList) {
                                        java.util.Map<String, Object> parkData = (java.util.Map<String, Object>) parkObj;
                                        String parkName = parkData.get("parkName").toString();
                                        String creatorName = parkData.get("creator") != null ? parkData.get("creator").toString() : "Unknown";

                                        System.out.println("        PARC: " + parkName + " (Créateur: " + creatorName + ")");

                                        // Chercher/créer le joueur
                                        PlayerEntity player = this.playerService.findByName(creatorName);

                                        // Créer le parc avec le joueur et la ville
                                        ParkEntity park = new ParkEntity(parkName, player, city);
                                        this.parkService.create(park);

                                        System.out.println("        PARC SAUVEGARDE");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("      ERREUR EXTRACTION DETAILS VILLE : " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("EXTRACTION DES VILLES TERMINEE");

        } catch (Exception e) {
            System.err.println("ERREUR LORS DE L'EXTRACTION DES VILLES : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private CityDifficulty cityDifficultyTranslator(String difficulty) {
        if (difficulty.equals("Facile")) {
            return CityDifficulty.EASY;
        }

        if (difficulty.equals("Modéré")) {
            return CityDifficulty.MEDIUM;
        }

        if (difficulty.equals("Difficile")) {
            return CityDifficulty.HARD;
        }

        throw new RuntimeException("La difficulté de la ville est inconnue");
    }

    private Long parseNumber(String numberStr) {
        // Parser les nombres avec espaces (ex: "250 000" -> 250000)
        String cleanedNumber = numberStr.replaceAll("[^0-9]", "");
        if (cleanedNumber.isEmpty()) {
            return null;
        }
        return Long.parseLong(cleanedNumber);
    }

}