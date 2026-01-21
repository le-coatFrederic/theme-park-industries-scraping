package com.fredlecoat.backend.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fredlecoat.backend.values.CityDifficulty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "city")
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CityDifficulty difficulty;

    private String country;

    private Long population;

    private Long availableSurface;
    
    private Long surface;

    private int maxHeight;

    private int parkPopulation;

    private int parkCapacity;

    private int priceByMeter;

    @CreationTimestamp
    private Instant createdOn;

    @UpdateTimestamp
    private Instant updatedOn;

    public CityEntity(
        String name,
        CityDifficulty difficulty,
        String country,
        Long population,
        Long availableSurface,
        Long surface,
        int maxHeight,
        int parkPopulation,
        int parkCapacity,
        int priceByMeter
    ) {
        this.name = name;
        this.difficulty = difficulty;
        this.country = country;
        this.population = population;
        this.availableSurface = availableSurface;
        this.surface = surface;
        this.maxHeight = maxHeight;
        this.parkPopulation = parkPopulation;
        this.parkCapacity = parkCapacity;
        this.priceByMeter = priceByMeter;
    }
}
