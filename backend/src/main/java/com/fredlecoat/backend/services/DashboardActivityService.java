package com.fredlecoat.backend.services;

import java.util.Map;

import com.fredlecoat.backend.entities.DashboardActivityEntity;

public interface DashboardActivityService {
    DashboardActivityEntity create(Map<String, Object> activityData);
}
