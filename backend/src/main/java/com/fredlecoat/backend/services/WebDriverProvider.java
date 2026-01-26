package com.fredlecoat.backend.services;

import org.openqa.selenium.WebDriver;

/**
 * Provides authenticated WebDriver instances for scraping.
 * Separates driver lifecycle management from authentication concerns.
 */
public interface WebDriverProvider {

    /**
     * Returns an authenticated WebDriver ready for scraping.
     * May return a cached instance if already initialized.
     */
    WebDriver getAuthenticatedDriver();

    /**
     * Checks if the driver is currently initialized and ready.
     */
    boolean isDriverReady();
}
