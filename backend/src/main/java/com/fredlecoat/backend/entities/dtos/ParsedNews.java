package com.fredlecoat.backend.entities.dtos;

import com.fredlecoat.backend.values.DashboardActivityType;

public record ParsedNews(
    String parkName,
    String cityName,
    Integer amount,
    String rideName,
    DashboardActivityType type
) {
}
