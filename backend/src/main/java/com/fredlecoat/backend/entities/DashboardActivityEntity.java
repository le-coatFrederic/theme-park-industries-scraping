package com.fredlecoat.backend.entities;

import java.time.LocalDateTime;


import com.fredlecoat.backend.values.DashboardActivityCategory;
import com.fredlecoat.backend.values.DashboardActivityType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DashboardActivityEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DashboardActivityCategory category;

    private LocalDateTime posted;

    @Enumerated(EnumType.STRING)
    private DashboardActivityType type;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = true)
    private PlayerEntity player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = true)
    private CityEntity city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_park_id", nullable = true)
    private ParkEntity actorPark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_park_id", nullable = true)
    private ParkEntity victimPark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = true)
    private RideEntity ride;

    private Long amount;

    public DashboardActivityEntity(DashboardActivityCategory category, LocalDateTime posted, DashboardActivityType type,
            String text, PlayerEntity player, CityEntity city, ParkEntity actorPark, ParkEntity victimPark,
            RideEntity ride, Long amount) {
        this.category = category;
        this.posted = posted;
        this.type = type;
        this.text = text;
        this.player = player;
        this.city = city;
        this.actorPark = actorPark;
        this.victimPark = victimPark;
        this.ride = ride;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DashboardActivityEntity [id=" + id + ", category=" + category + ", posted=" + posted + ", type=" + type
                + ", text=" + text + ", player=" + player + ", city=" + city + ", actorPark=" + actorPark
                + ", victimPark=" + victimPark + ", ride=" + ride + ", amount=" + amount + "]";
    }

}
