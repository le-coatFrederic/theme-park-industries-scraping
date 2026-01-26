package com.fredlecoat.backend.services.scrapers;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.configuration.ScrapingConfig;
import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.utils.ScrapingParser;
import com.fredlecoat.backend.values.RideType;

/**
 * Scraper for extracting ride/attraction data from the attractions store.
 *
 * Responsibilities:
 * - Navigate to attractions page and open the store modal
 * - Extract ride details (name, price, type, hype, surface, etc.)
 * - Delegate persistence to RideService
 */
@Component
public class RideScraper extends BaseScraper {

    private static final String MODAL_SELECTOR = "#attraction-store-modal";

    private final RideService rideService;

    @Autowired
    public RideScraper(RideService rideService) {
        this.rideService = rideService;
    }

    @Override
    public void scrape() {
        scrapeAllRides();
    }

    public void scrapeAllRides() {
        System.out.println("DEBUT SCRAPING DES ATTRACTIONS");

        try {
            navigateTo(ScrapingConfig.ATTRACTIONS_PAGE);
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

            openShopModal(wait);
            extractAndSaveRides();

            System.out.println("SCRAPING DES ATTRACTIONS TERMINE");

        } catch (Exception e) {
            System.err.println("ERREUR SCRAPING ATTRACTIONS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openShopModal(WebDriverWait wait) {
        WebElement shopButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(ScrapingConfig.Selectors.OPEN_STORE_BUTTON))
        );

        System.out.println("OUVERTURE DE LA MODALE BOUTIQUE");
        executeScript("arguments[0].click();", shopButton);

        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(MODAL_SELECTOR))
        );
    }

    private void extractAndSaveRides() {
        System.out.println("EXTRACTION DES ATTRACTIONS");

        try {
            List<Map<String, Object>> cardsData = extractRidesData();
            System.out.println("NOMBRE D'ATTRACTIONS TROUVEES: " + cardsData.size());

            for (Map<String, Object> card : cardsData) {
                saveRide(card);
            }

        } catch (Exception e) {
            System.err.println("ERREUR EXTRACTION DES ATTRACTIONS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRidesData() {
        return (List<Map<String, Object>>) executeScript(RideExtractionScripts.ATTRACTIONS_LIST);
    }

    private void saveRide(Map<String, Object> card) {
        try {
            RideEntity ride = createRideFromData(card);
            rideService.save(ride);
            System.out.println("ATTRACTION SAUVEGARDEE: " + ride.getName() + " de " + ride.getBrand());
        } catch (Exception e) {
            System.err.println("ERREUR SAUVEGARDE ATTRACTION: " + e.getMessage());
        }
    }

    private RideEntity createRideFromData(Map<String, Object> card) {
        String name = getStringValue(card, "name");
        String constructor = getStringValue(card, "constructor");
        RideType type = parseRideType(getStringValue(card, "type"));

        int hype = parseIntAttribute(card, "hype");
        long price = parseLongAttribute(card, "price");
        long surface = parseSurface(getStringValue(card, "description"));

        String imageUrl = ScrapingParser.normalizeImageUrl(getStringValue(card, "imageUrl"));

        return new RideEntity(type, -1, hype, name, constructor, price, surface, imageUrl);
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    private RideType parseRideType(String type) {
        return "flatride".equals(type) ? RideType.FLAT_RIDE : RideType.COASTER;
    }

    private int parseIntAttribute(Map<String, Object> card, String key) {
        try {
            return Integer.parseInt(getStringValue(card, key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long parseLongAttribute(Map<String, Object> card, String key) {
        try {
            return Long.parseLong(getStringValue(card, key));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private long parseSurface(String description) {
        if (description == null || description.isEmpty()) {
            return 0L;
        }
        String cleaned = description
            .replace("m²", "")
            .replace(" ", "")
            .trim();
        try {
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Executes JavaScript with arguments.
     */
    private void executeScript(String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script, args);
    }

    /**
     * JavaScript extraction scripts for ride data.
     */
    private static final class RideExtractionScripts {

        static final String ATTRACTIONS_LIST = """
            return Array.from(document.querySelectorAll('.attraction-card')).map(card => ({
                constructor: card.getAttribute('data-constructor'),
                type: card.getAttribute('data-type'),
                price: card.getAttribute('data-price'),
                hype: card.getAttribute('data-hype'),
                reliability: card.getAttribute('data-reliability'),
                name: card.querySelector('.attraction-card__title')?.textContent?.trim(),
                description: card.querySelector('.attraction-card__description strong:last-child')?.textContent?.trim(),
                manufacturer: card.querySelector('.attraction-card__manufacturer')?.textContent?.trim(),
                imageUrl: card.querySelector('.attraction-card__image')?.getAttribute('src')
            }));
            """;

        private RideExtractionScripts() {}
    }
}
