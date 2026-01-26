package com.fredlecoat.backend.services.scrapers;

import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.fredlecoat.backend.configuration.WebSiteAccessConfig;
import com.fredlecoat.backend.services.WebDriverProvider;

/**
 * Base class for all scrapers providing common functionality.
 *
 * Responsibilities:
 * - WebDriver access through WebDriverProvider
 * - URL building from configuration
 * - JavaScript execution helpers
 * - Common delay/wait handling
 */
public abstract class BaseScraper {

    @Autowired
    protected WebSiteAccessConfig accessConfig;

    @Autowired
    protected WebDriverProvider webDriverProvider;

    /**
     * Returns the authenticated WebDriver for scraping.
     */
    protected WebDriver getDriver() {
        return webDriverProvider.getAuthenticatedDriver();
    }

    /**
     * Builds a full URL from a relative path.
     */
    protected String buildUrl(String relativePath) {
        return accessConfig.getUrl() + relativePath;
    }

    /**
     * Navigates to a page and waits for it to load.
     */
    protected void navigateTo(String relativePath) {
        getDriver().get(buildUrl(relativePath));
    }

    /**
     * Executes JavaScript and returns the result as a Map.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> executeScriptAsMap(String script) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (Map<String, Object>) js.executeScript(script);
    }

    /**
     * Executes JavaScript and returns the result.
     */
    protected Object executeScript(String script) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return js.executeScript(script);
    }

    /**
     * Pauses execution for the specified duration.
     */
    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Template method for the main scraping operation.
     * Subclasses should implement their specific scraping logic.
     */
    public abstract void scrape();
}
