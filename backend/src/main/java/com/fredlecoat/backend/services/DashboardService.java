package com.fredlecoat.backend.services;

import java.util.List;
import java.util.Map;

import com.fredlecoat.backend.entities.DashboardActivityEntity;

public interface DashboardService {
    Map<String, String> getPersonalData();
    List<DashboardActivityEntity> getDashboardActivities();
}
