package com.fredlecoat.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.services.LoginService;

import jakarta.annotation.PreDestroy;

@Component
public class WebDriverCleanup {

    @Autowired
    private LoginService loginService;

    @PreDestroy
    public void cleanup() {
        if (loginService.getDriver() != null) {
            loginService.getDriver().quit(); 
        }
    }
}