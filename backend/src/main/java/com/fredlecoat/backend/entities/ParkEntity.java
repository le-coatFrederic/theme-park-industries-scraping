package com.fredlecoat.backend.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "park")
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

    @CreationTimestamp
    private Instant createdOn;

    @UpdateTimestamp
    private Instant updatedOn;

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

    public void addRide(RideEntity ride) {
        this.rides.add(ride);
        ride.addPark(this);
    }
}
