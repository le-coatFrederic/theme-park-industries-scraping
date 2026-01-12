package com.fredlecoat.backend.entities.dtos;

public record DashboardActivityTextScrapedDtoRequest(
    String category,
    String date,
    String event
) {

}
