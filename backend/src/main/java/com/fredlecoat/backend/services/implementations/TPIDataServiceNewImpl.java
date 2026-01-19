package com.fredlecoat.backend.services.implementations;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.services.TPIDataService;
import com.fredlecoat.backend.values.RideType;

@Service
public class TPIDataServiceNewImpl implements TPIDataService {

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RideService rideService;

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
        // TODO 
          
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

}

