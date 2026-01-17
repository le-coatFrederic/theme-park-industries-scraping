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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.entities.dtos.DashboardActivityTextScrapedDtoRequest;
import com.fredlecoat.backend.entities.mappers.DashboardActivityMapper;
import com.fredlecoat.backend.services.DashboardService;
import com.fredlecoat.backend.services.LoginService;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SeleniumTPIOldInterfaceDashboardServiceImpl implements DashboardService{
    @Autowired
    private ChromeOptions chromeOptions;

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private DashboardActivityMapper mapper;
    
    //@Value("${scraper.timeout:10}")
    private int timeout = 10;
    
    public SeleniumTPIOldInterfaceDashboardServiceImpl(ChromeOptions chromeOptions, LoginService loginService) {
        this.chromeOptions = chromeOptions;
        this.loginService = loginService;
    }
    
    @Override
    public Map<String, String> getPersonalData() {
        WebDriver driver = loginService.getDriver();
        Map<String, String> personalData = new HashMap<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));        
        try {           
            driver.get(this.accessConfig.getUrl() + "game/dashboard.php");

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

            WebElement characterSection = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("section.character-card"))
            );

            personalData.put("money", getElementText(characterSection, ".character-card__money-value"));
            personalData.put("level", getElementText(characterSection, ".character-card__level-value"));
            personalData.put("experience", getElementText(characterSection, ".character-card__exp"));
        } catch (Exception e) {
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        
        return personalData;
    }
    
    @Override
    public List<DashboardActivityEntity> getDashboardActivities() {
        List<DashboardActivityEntity> activities = new ArrayList<>();
        
        WebDriver driver = loginService.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));        
        try {           
            driver.get(this.accessConfig.getUrl() + "game/dashboard.php");

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

            WebElement newsSection = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.news-journal__content"))
            );

            for(WebElement news: newsSection.findElements(By.cssSelector("div.news-journal__item"))) {
                DashboardActivityTextScrapedDtoRequest dto = new DashboardActivityTextScrapedDtoRequest(
                    getElementText(newsSection, ".news-journal__badge"),
                    getElementText(newsSection, ".news-journal__date"),
                    getElementText(newsSection, ".news-journal__text")
                );

                activities.add(mapper.toEntity(dto));
            }
        } catch (Exception e) {
        } finally {
            if (driver != null) {
                driver.quit();
            }
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
