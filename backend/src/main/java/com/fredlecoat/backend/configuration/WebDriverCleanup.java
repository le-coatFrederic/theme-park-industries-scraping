package com.fredlecoat.backend.configuration;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class WebDriverCleanup {
    private final WebDriver driver;

    public WebDriverCleanup(WebDriver driver) {
        this.driver = driver;
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit(); 
        }
    }
}