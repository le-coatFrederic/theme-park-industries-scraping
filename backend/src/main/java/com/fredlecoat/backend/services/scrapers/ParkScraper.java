package com.fredlecoat.backend.services.scrapers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.configuration.ScrapingConfig;
import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.utils.ScrapingParser;

/**
 * Scraper for extracting park data from individual park pages.
 *
 * Responsibilities:
 * - Iterate through park IDs and fetch park pages
 * - Extract park details (name, stats, attractions)
 * - Manage scraping state (running, current ID, errors)
 * - Delegate persistence to ParkService
 *
 * Note: This scraper runs in a background thread to avoid blocking.
 */
@Component
public class ParkScraper extends BaseScraper {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger currentParkId = new AtomicInteger(1);

    private final ParkService parkService;
    private final CityService cityService;

    @Autowired
    public ParkScraper(ParkService parkService, CityService cityService) {
        this.parkService = parkService;
        this.cityService = cityService;
    }

    @Override
    public void scrape() {
        scrapeAllParks();
    }

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

        ScrapingProgress progress = new ScrapingProgress();
        System.out.println("DEBUT SCRAPING DES PARCS (ID de depart: " + startId + ")");

        try {
            while (shouldContinueScraping(progress)) {
                int parkId = currentParkId.getAndIncrement();
                processPark(parkId, progress);
                sleep(ScrapingConfig.DELAY_BETWEEN_PARKS_MS);
            }

            logScrapingEnd(progress);

        } catch (Exception e) {
            System.err.println("ERREUR FATALE SCRAPING: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isRunning.set(false);
            System.out.println("SCRAPING DES PARCS TERMINE");
        }
    }

    private boolean shouldContinueScraping(ScrapingProgress progress) {
        return isRunning.get()
            && progress.consecutiveErrors < ScrapingConfig.MAX_CONSECUTIVE_ERRORS
            && progress.successCount < ScrapingConfig.MAX_PARK_AMOUNT;
    }

    private void processPark(int parkId, ScrapingProgress progress) {
        try {
            boolean success = scrapePark(parkId);

            if (success) {
                progress.consecutiveErrors = 0;
                progress.successCount++;
            } else {
                progress.consecutiveErrors++;
                System.out.println("  Parc " + parkId + " non trouve (" +
                    progress.consecutiveErrors + "/" + ScrapingConfig.MAX_CONSECUTIVE_ERRORS + ")");
            }

        } catch (Exception e) {
            progress.consecutiveErrors++;
            System.err.println("ERREUR PARC " + parkId + ": " + e.getMessage());
        }
    }

    private void logScrapingEnd(ScrapingProgress progress) {
        if (progress.consecutiveErrors >= ScrapingConfig.MAX_CONSECUTIVE_ERRORS) {
            System.out.println("ARRET: " + ScrapingConfig.MAX_CONSECUTIVE_ERRORS + " erreurs consecutives");
        }
    }

    private boolean scrapePark(int parkId) {
        navigateTo(ScrapingConfig.PARK_PAGE_TEMPLATE + parkId);
        sleep(ScrapingConfig.PAGE_LOAD_DELAY_MS);

        Map<String, Object> parkData = executeScriptAsMap(ParkExtractionScripts.PARK_DETAILS);

        if (parkData == null || parkData.get("name") == null) {
            return false;
        }

        savePark(parkData, parkId);
        return true;
    }

    private void savePark(Map<String, Object> data, int parkId) {
        if (parkId <= 0) {
            System.err.println("ERREUR: parkId invalide: " + parkId);
            return;
        }

        ParkEntity park = createOrUpdatePark(data, parkId);
        park = linkAttractions(park, data);

        logParkSaved(park, data);
    }

    private ParkEntity createOrUpdatePark(Map<String, Object> data, int parkId) {
        String parkName = data.get("name").toString();
        String location = data.get("location") != null ? data.get("location").toString() : "";
        CityEntity city = findCityFromLocation(location);

        Long capital = parseLongOrDefault(ScrapingParser.parseMoney(data.get("capital")), 0L);
        Long socialCapital = parseLongOrDefault(ScrapingParser.parseMoney(data.get("socialCapital")), 0L);
        Integer yesterdayVisitors = ScrapingParser.parseInteger(data.get("yesterdayVisitors"));
        Integer usedSurface = ScrapingParser.parseIntegerOrDefault(data.get("usedSurface"), 0);
        Integer note = ScrapingParser.parseIntegerOrDefault(data.get("note"), 0);

        System.out.println("PARC #" + parkId + ": " + parkName);

        ParkEntity park = new ParkEntity(parkId, parkName, city, capital, socialCapital, yesterdayVisitors, usedSurface, note);
        return parkService.save(park);
    }

    private Long parseLongOrDefault(Integer value, Long defaultValue) {
        return value != null ? value.longValue() : defaultValue;
    }

    @SuppressWarnings("unchecked")
    private ParkEntity linkAttractions(ParkEntity park, Map<String, Object> data) {
        List<Map<String, Object>> attractions = (List<Map<String, Object>>) data.get("attractions");

        if (attractions == null) {
            return park;
        }

        for (Map<String, Object> attraction : attractions) {
            park = linkAttractionToPark(park, attraction);
        }

        return park;
    }

    private void logParkSaved(ParkEntity park, Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> attractions = (List<Map<String, Object>>) data.get("attractions");
        System.out.println("  -> " + (attractions != null ? attractions.size() : 0) + " attractions liees");
    }

    private CityEntity findCityFromLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        // Remove location pin emoji and extract city name
        String cityName = location.replace("\uD83D\uDCCD", "").trim();
        if (cityName.contains(",")) {
            cityName = cityName.split(",")[0].trim();
        }

        return cityService.findByName(cityName);
    }

    private ParkEntity linkAttractionToPark(ParkEntity park, Map<String, Object> attractionData) {
        Object imageUrlObj = attractionData.get("imageUrl");
        if (imageUrlObj == null) {
            return park;
        }

        String imageUrl = ScrapingParser.normalizeImageUrl(imageUrlObj.toString());
        if (imageUrl != null) {
            return parkService.addRideByImageUrl(park, imageUrl);
        }
        return park;
    }

    /**
     * Helper class to track scraping progress.
     */
    private static class ScrapingProgress {
        int consecutiveErrors = 0;
        int successCount = 0;
    }

    /**
     * JavaScript extraction scripts for park data.
     */
    private static final class ParkExtractionScripts {

        static final String PARK_DETAILS = """
            return (function() {
                const hero = document.querySelector('.park-hero');
                if (!hero) return null;

                const titleElem = hero.querySelector('.park-hero__title');
                const name = titleElem ? titleElem.textContent.trim() : null;

                if (!name) return null;

                const locationElem = hero.querySelector('.park-hero__location');
                const location = locationElem ? locationElem.textContent.trim() : null;

                let owner = null;

                // Extract stats from park-stats-section__card elements
                const stats = {};
                const statCards = document.querySelectorAll('.park-stats-section__card');
                for (const card of statCards) {
                    const titleEl = card.querySelector('.park-stats-section__card-title');
                    const valueEl = card.querySelector('.park-stats-section__card-value');
                    if (titleEl && valueEl) {
                        const title = titleEl.textContent.trim();
                        const value = valueEl.textContent.trim();
                        stats[title] = value;
                    }
                }

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
                    attractions: attractions,
                    capital: stats['Trésorerie'] || null,
                    socialCapital: stats['Capital social'] || null,
                    yesterdayVisitors: stats['Visiteurs hier'] || null,
                    usedSurface: stats['Surface utilisée'] || null,
                    note: stats['Note'] || null
                };
            })();
            """;

        private ParkExtractionScripts() {}
    }
}
