package com.fredlecoat.backend.services.scrapers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.services.RideService;

@Component
public class ParkScraper {

    private static final String PARK_PAGE_TEMPLATE = "game/park/fake/monpark.php?id=";
    private static final int DELAY_BETWEEN_PARKS_MS = 1500;
    private static final int MAX_CONSECUTIVE_ERRORS = 10;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger currentParkId = new AtomicInteger(1);

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CityService cityService;

    @Autowired
    private RideService rideService;

    public void scrapeAllParks() {
        scrapeAllParks(1);
    }

    public void scrapeAllParks(int startId) {
        if (isRunning.get()) {
            System.out.println("SCRAPING DEJA EN COURS");
            return;
        }

        Thread scraperThread = new Thread(() -> runScrapingLoop(startId));
        scraperThread.setName("ParkScraper-Thread");
        scraperThread.setDaemon(true);
        scraperThread.start();
    }

    public void stopScraping() {
        isRunning.set(false);
        System.out.println("ARRET DU SCRAPING DEMANDE");
    }

    public int getCurrentParkId() {
        return currentParkId.get();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    private void runScrapingLoop(int startId) {
        isRunning.set(true);
        currentParkId.set(startId);
        int consecutiveErrors = 0;

        System.out.println("DEBUT SCRAPING DES PARCS (ID de depart: " + startId + ")");

        try {
            WebDriver driver = loginService.getDriver();

            while (isRunning.get() && consecutiveErrors < MAX_CONSECUTIVE_ERRORS) {
                int parkId = currentParkId.getAndIncrement();

                try {
                    boolean success = scrapePark(driver, parkId);

                    if (success) {
                        consecutiveErrors = 0;
                    } else {
                        consecutiveErrors++;
                        System.out.println("  Parc " + parkId + " non trouve (" + consecutiveErrors + "/" + MAX_CONSECUTIVE_ERRORS + ")");
                    }

                    Thread.sleep(DELAY_BETWEEN_PARKS_MS);

                } catch (Exception e) {
                    consecutiveErrors++;
                    System.err.println("ERREUR PARC " + parkId + ": " + e.getMessage());
                }
            }

            if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                System.out.println("ARRET: " + MAX_CONSECUTIVE_ERRORS + " erreurs consecutives");
            }

        } catch (Exception e) {
            System.err.println("ERREUR FATALE SCRAPING: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isRunning.set(false);
            System.out.println("SCRAPING DES PARCS TERMINE");
        }
    }

    private boolean scrapePark(WebDriver driver, int parkId) {
        String url = accessConfig.getUrl() + PARK_PAGE_TEMPLATE + parkId;
        driver.get(url);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;

        @SuppressWarnings("unchecked")
        Map<String, Object> parkData = (Map<String, Object>) js.executeScript(buildParkExtractionScript());

        if (parkData == null || parkData.get("name") == null) {
            return false;
        }

        savePark(parkData, parkId);
        return true;
    }

    private void savePark(Map<String, Object> data, int parkId) {
        String parkName = data.get("name").toString();
        String ownerName = data.get("owner") != null ? data.get("owner").toString() : "Unknown";
        String location = data.get("location") != null ? data.get("location").toString() : "";

        System.out.println("PARC #" + parkId + ": " + parkName + " (Owner: " + ownerName + ")");

        ParkEntity park = parkService.findByExternalId(parkId);

        if (park == null) {
            PlayerEntity owner = playerService.findByName(ownerName);
            CityEntity city = findCityFromLocation(location);
            park = new ParkEntity(parkId, parkName, owner, city);
            park = parkService.create(park);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> attractions = (List<Map<String, Object>>) data.get("attractions");

        if (attractions != null) {
            for (Map<String, Object> attraction : attractions) {
                linkAttractionToPark(park, attraction);
            }
        }

        System.out.println("  -> " + (attractions != null ? attractions.size() : 0) + " attractions liees");
    }

    private CityEntity findCityFromLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        String cityName = location.replace("\uD83D\uDCCD", "").trim();
        if (cityName.contains(",")) {
            cityName = cityName.split(",")[0].trim();
        }

        return cityService.findByName(cityName);
    }

    private void linkAttractionToPark(ParkEntity park, Map<String, Object> attractionData) {
        String imageUrl = attractionData.get("imageUrl") != null
            ? normalizeImageUrl(attractionData.get("imageUrl").toString())
            : null;

        if (imageUrl != null) {
            var ride = rideService.findByImageUrl(imageUrl);
            if (ride != null) {
                park.addRide(ride);
            }
        }
    }

    private String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        int attractionsIndex = rawUrl.indexOf("attractions/");
        if (attractionsIndex != -1) {
            return rawUrl.substring(attractionsIndex + "attractions/".length());
        }
        return rawUrl;
    }

    private String buildParkExtractionScript() {
        return """
            return (function() {
                const hero = document.querySelector('.park-hero');
                if (!hero) return null;

                const titleElem = hero.querySelector('.park-hero__title');
                const name = titleElem ? titleElem.textContent.trim() : null;

                if (!name) return null;

                const locationElem = hero.querySelector('.park-hero__location');
                const location = locationElem ? locationElem.textContent.trim() : null;

                let owner = null;

                const attractions = [];
                const cards = document.querySelectorAll('.park-attraction-card');

                for (const card of cards) {
                    const imgEl = card.querySelector('.park-attraction-card__image img');
                    const imageUrl = imgEl ? imgEl.getAttribute('src') : null;

                    attractions.push({
                        imageUrl: imageUrl
                    });
                }

                return {
                    name: name,
                    location: location,
                    owner: owner,
                    attractions: attractions
                };
            })();
            """;
    }
}
