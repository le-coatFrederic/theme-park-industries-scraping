package com.fredlecoat.backend.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@AllArgsConstructor
public class ParkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity owner;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @ManyToMany
    @JoinTable(
        name = "parks_rides",
        joinColumns = @JoinColumn(name = "park_id"),
        inverseJoinColumns = @JoinColumn(name = "ride_id")
    )
    private Set<RideEntity> rides;

    public ParkEntity(
        String name,
        PlayerEntity owner,
        CityEntity city
    ) {
        this.name = name;
        this.owner = owner;
        this.city = city;

        this.rides = new HashSet<>();
    }
}
