package com.fredlecoat.backend.entities.dtos;

import com.fredlecoat.backend.values.DashboardActivityType;

public record ParsedNews(
    String playerName,
    String cityName,
    String actorParkName,
    String victimParkName,
    String rideName,
    Integer amount,
    DashboardActivityType type
) {
}
