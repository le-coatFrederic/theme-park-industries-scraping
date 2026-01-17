package com.fredlecoat.backend.configuration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    private WebDriver driver;

    @Bean
    public WebDriver webDriver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--user-agent=Mozilla/5.0...");
            this.driver = new ChromeDriver(options);
        }
        return driver;
    }

    // Fermer le driver au shutdown de l'appli
    @Bean
    public WebDriverCleanup webDriverCleanup(WebDriver driver) {
        return new WebDriverCleanup(driver);
    }

}
