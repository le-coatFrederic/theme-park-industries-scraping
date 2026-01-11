package com.fredlecoat.backend.entities;

import com.fredlecoat.backend.values.CityCountry;
import com.fredlecoat.backend.values.CityDifficulty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CityDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    private CityCountry country;

    private Long population;
    
    private Long surface;

    private int maxHeight;

    private int parkCapacity;

    private int priceByMeter;

    public CityEntity(
        String name,
        CityDifficulty difficulty,
        CityCountry country,
        Long population,
        Long surface,
        int maxHeight,
        int parkCapacity,
        int priceByMeter
    ) {
        this.name = name;
        this.difficulty = difficulty;
        this.country = country;
        this.population = population;
        this.surface = surface;
        this.maxHeight = maxHeight;
        this.parkCapacity = parkCapacity;
        this.priceByMeter = priceByMeter;
    }
}
