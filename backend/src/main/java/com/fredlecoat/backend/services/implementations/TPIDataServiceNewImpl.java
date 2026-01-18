package com.fredlecoat.backend.services.implementations;

import static org.mockito.Mockito.timeout;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.services.TPIDataService;

@Service
public class TPIDataServiceNewImpl implements TPIDataService {

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private WebDriver driver;

    @Override
    public void getAllRidesData() {
        System.out.println("DEBUT GETALLRIDESDATA()");

        try {
            driver.get(this.accessConfig.getUrl() + "game/park/attractions.php");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            WebElement shopButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("open-attraction-store-btn"))
            );

            shopButton.click();

            WebElement listOfRideBrands = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("attraction-store-modal__pills"))
            );

            
        } catch (Exception e) {
        }
    }

    @Override
    public void getAllCitiesData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCitiesData'");
    }

    @Override
    public void getAllParksData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllParksData'");
    }

}
