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

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.values.RideType;

@Component
public class RideScraper {

    private static final String ATTRACTIONS_PAGE = "game/park/attractions.php";
    private static final String SHOP_BUTTON_SELECTOR = "button#open-attraction-store-btn";
    private static final String MODAL_SELECTOR = "#attraction-store-modal";

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RideService rideService;

    public void scrapeAllRides() {
        System.out.println("DEBUT SCRAPING DES ATTRACTIONS");

        try {
            WebDriver driver = loginService.getDriver();
            driver.get(accessConfig.getUrl() + ATTRACTIONS_PAGE);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement modal = openShopModal(driver, wait);
            extractAndSaveRides(modal);

        } catch (Exception e) {
            System.err.println("ERREUR SCRAPING ATTRACTIONS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebElement openShopModal(WebDriver driver, WebDriverWait wait) {
        WebElement shopButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(SHOP_BUTTON_SELECTOR))
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", shopButton);

        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(MODAL_SELECTOR))
        );
    }

    private void extractAndSaveRides(WebElement modal) {
        WebDriver driver = ((org.openqa.selenium.remote.RemoteWebElement) modal).getWrappedDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = buildExtractionScript();

        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cardsData = (List<Map<String, Object>>) js.executeScript(script);
            System.out.println("NOMBRE D'ATTRACTIONS TROUVEES: " + cardsData.size());

            for (Map<String, Object> card : cardsData) {
                RideEntity ride = createRideFromData(card);
                rideService.create(ride);
                System.out.println("ATTRACTION SAUVEGARDEE: " + ride.getName());
            }

        } catch (Exception e) {
            System.err.println("ERREUR EXTRACTION DES ATTRACTIONS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private RideEntity createRideFromData(Map<String, Object> card) {
        RideType type = "flatride".equals(card.get("type").toString())
            ? RideType.FLAT_RIDE
            : RideType.COASTER;

        String surfaceStr = card.get("description").toString()
            .replace("m²", "")
            .trim()
            .replace(" ", "");

        return new RideEntity(
            type,
            -1,
            Integer.parseInt(card.get("hype").toString()),
            card.get("name").toString(),
            card.get("constructor").toString(),
            Long.parseLong(card.get("price").toString()),
            Long.valueOf(surfaceStr)
        );
    }

    private String buildExtractionScript() {
        return """
            return Array.from(document.querySelectorAll('.attraction-card')).map(card => ({
                constructor: card.getAttribute('data-constructor'),
                type: card.getAttribute('data-type'),
                price: card.getAttribute('data-price'),
                hype: card.getAttribute('data-hype'),
                reliability: card.getAttribute('data-reliability'),
                name: card.querySelector('.attraction-card__title')?.textContent?.trim(),
                description: card.querySelector('.attraction-card__description strong:last-child')?.textContent?.trim(),
                manufacturer: card.querySelector('.attraction-card__manufacturer')?.textContent?.trim()
            }));
            """;
    }
}
