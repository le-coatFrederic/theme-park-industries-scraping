package com.fredlecoat.backend.entities.dtos;

public record MainPlayerRequest(
    String name,
    int money,
    int level,
    int experience
) {
    
}
