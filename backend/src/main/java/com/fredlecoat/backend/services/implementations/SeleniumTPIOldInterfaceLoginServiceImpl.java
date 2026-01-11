package com.fredlecoat.backend.services.implementations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.services.LoginService;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SeleniumTPIOldInterfaceLoginServiceImpl implements LoginService{

    @Autowired
    private ChromeOptions chromeOptions;
    
    @Autowired
    private WebSiteAccessConfig accessConfig;

    //@Value("${scraper.global.timeout}")
    private int timeout = 10;

    public SeleniumTPIOldInterfaceLoginServiceImpl(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
    }

    @Override
    public WebDriver getDriver() {
        WebDriver driver = null;
        Map<String, String> cookies = new HashMap<>();
        try {
            driver = new ChromeDriver(chromeOptions);
            driver.get(this.accessConfig.getUrl() + "play.php");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            
            // Attendre que la page soit chargée
            wait.until(webDriver -> 
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );
            
            WebElement usernameField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("login-email"))
            );
            WebElement passwordField = driver.findElement(By.id("login-password"));
            WebElement loginButton = driver.findElement(By.cssSelector("form.auth-form button.form-submit"));
            
            // Remplir le formulaire
            usernameField.sendKeys(this.accessConfig.getEmail());
            passwordField.sendKeys(this.accessConfig.getPassword());
            loginButton.click();
            
            // Attendre la redirection après login
            wait.until(ExpectedConditions.urlContains("dashboard"));
        } catch (Exception e) {
            throw new RuntimeException("Échec de la connexion", e);
        }

        return driver;
    }

}
