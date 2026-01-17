package com.fredlecoat.backend.services.implementations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.services.DashboardActivityService;
import com.fredlecoat.backend.services.DashboardService;
import com.fredlecoat.backend.services.LoginService;

public class SeleniumTPINewInterfaceDashboardServiceImpl implements DashboardService{

    @Autowired
    private LoginService loginService;

    @Autowired
    private DashboardActivityService dashboardActivityService;
    
    private String dashboardUrl = "https://themeparkindustries.com/tpiv4/game/monbureau.php";
    
    private int timeout = 10;

    private WebDriver driver;
    
    @Override
    public Map<String, String> getPersonalData() {
        Map<String, String> personalData = new HashMap<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));        
        try {           
            driver.get(dashboardUrl);

            ((JavascriptExecutor) driver).executeScript("""
                const style = document.createElement('style');
                style.innerHTML = `
                    * {
                        transition: none !important;
                        animation: none !important;
                    }
                `;
                document.head.appendChild(style);
            """);

            WebElement personalButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-app='personnage']"))
            );

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", personalButton);

            wait.until(ExpectedConditions.attributeContains(personalButton, "class", "active"));

            WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".os-window[data-app='personnage']")
                )
            );

            WebElement iframe = wait.until(
                ExpectedConditions.presenceOfNestedElementLocatedBy(
                    modal,
                    By.tagName("iframe")
                )
            );

            driver.switchTo().frame(iframe);

            WebElement pageContent = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".personnage-container")
                )
            );

            personalData.put("money", getElementText(pageContent, "div.money-value"));
            personalData.put("level", getElementText(pageContent, ".level-value"));
            personalData.put("experience", getElementText(pageContent, ".exp-text"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return personalData;
    }
    
    @Override
    public List<DashboardActivityEntity> getDashboardActivities() {
        List<DashboardActivityEntity> activities = new ArrayList<>();
        
        try {
            driver = loginService.getDriver();
            driver.get(dashboardUrl);

            System.out.println("ON A EU LA PAGE DASHBOARD");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.until(webDriver -> 
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );
            
            List<WebElement> activityElements = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".news-journal__item"))
            );

            System.out.println("Nombre d'activités trouvées: " + activityElements.size());

            for (int i = 0; i < activityElements.size(); i++) {
                try {
                    WebElement element = activityElements.get(i);
                    DashboardActivityEntity activity = this.dashboardActivityService.create(element);
                    activities.add(activity);
                    System.out.println("Activité " + (i + 1) + " créée avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la création de l'activité " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                    // Continue to next element instead of breaking
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des activités du dashboard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return activities;
    }
    
    private String getElementText(WebElement element, String cssSelector) {
        try {
            return element.findElement(By.cssSelector(cssSelector)).getText();
        } catch (NoSuchElementException e) {
            return "Unfindable";
        }
    }
}
