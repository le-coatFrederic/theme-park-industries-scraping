package com.fredlecoat.backend.services;

import org.openqa.selenium.WebDriver;

/**
 * Service responsible for authenticating to the TPI website.
 * Extends WebDriverProvider to provide authenticated driver access.
 */
public interface LoginService extends WebDriverProvider {

    /**
     * @deprecated Use {@link #getAuthenticatedDriver()} instead
     */
    @Deprecated
    WebDriver getDriver();

    @Override
    default WebDriver getAuthenticatedDriver() {
        return getDriver();
    }

    @Override
    default boolean isDriverReady() {
        return getDriver() != null;
    }
}
