package com.fredlecoat.backend.services;

import org.openqa.selenium.WebElement;

import com.fredlecoat.backend.entities.DashboardActivityEntity;

public interface DashboardActivityService {
    public DashboardActivityEntity create(WebElement element);
}
