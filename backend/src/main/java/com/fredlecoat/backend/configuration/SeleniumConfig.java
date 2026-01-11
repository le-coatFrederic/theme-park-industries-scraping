package com.fredlecoat.backend.configuration;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {
    
    @Bean
    public ChromeOptions chromeOptions() {
        // Pas besoin de WebDriverManager.chromedriver().setup()
        // Selenium Manager gère automatiquement le driver
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Nouveau mode headless
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        
        // Pour les sites avec sécurité stricte
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        return options;
    }

}
