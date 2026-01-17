package com.fredlecoat.backend.services.implementations;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.services.LoginService;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SeleniumTPINewInterfaceLoginServiceImpl implements LoginService{

    @Autowired
    private WebSiteAccessConfig accessConfig;

    @Autowired
    private WebDriver driver;

    //@Value("${scraper.global.timeout}")
    private int timeout = 10;

    @Override
    public WebDriver getDriver() {

        System.out.println("DEBUT TENTATIVE CONNEXION");

        try {
            driver.get(this.accessConfig.getUrl() + "play.php");

            System.out.println("ACCES A LA PAGE " + this.accessConfig.getUrl() + "play.php");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            
            // Attendre que la page soit chargée
            wait.until(webDriver -> 
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );

            System.out.println("TROUVER LE FORMULAIRE DE CONNEXION");
            
            WebElement usernameField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("login-email"))
            );
            WebElement passwordField = driver.findElement(By.id("login-password"));
            WebElement loginButton = driver.findElement(By.cssSelector("form.auth-form button.form-submit"));

            System.out.println("FORMULAIRE TROUVE, COMPOSANTS ENREGISTRES");
            
            // Remplir le formulaire
            usernameField.sendKeys(this.accessConfig.getEmail());
            passwordField.sendKeys(this.accessConfig.getPassword());
            loginButton.click();

            System.out.println("ENVOI DU FORMULAIRE DE CONNEXION");
            
            // Attendre la redirection après login
            wait.until(ExpectedConditions.urlContains("dashboard"));

            System.out.println("CONNEXION REUSSIE");
        } catch (Exception e) {
            throw new RuntimeException("Échec de la connexion", e);
        }

        return driver;
    }

}
