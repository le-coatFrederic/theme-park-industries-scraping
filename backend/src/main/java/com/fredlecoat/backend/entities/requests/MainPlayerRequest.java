package com.fredlecoat.backend.entities.requests;

public record MainPlayerRequest(
    String name,
    int money,
    int level,
    int experience
) {
    
}
