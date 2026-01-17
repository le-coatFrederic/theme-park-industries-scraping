package com.fredlecoat.backend.entities;

import java.util.HashSet;
import java.util.Set;

import com.fredlecoat.backend.values.RideBrand;
import com.fredlecoat.backend.values.RideType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RideType type;

    private int maxCapacityByHour;

    private int hype;

    private String name;

    private String brand;

    private Long price;

    private Long surface;

    private int height;

    @ManyToMany(mappedBy = "rides")
    private Set<ParkEntity> parks;

    public RideEntity(
        RideType type,
        int maxCapacityByHour,
        int hype,
        String name,
        String brand,
        Long price,
        Long surface,
        int height
    ) {
        this.type = type;
        this.maxCapacityByHour = maxCapacityByHour;
        this.hype = hype;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.surface = surface;
        this.height = height;

        this.parks = new HashSet<>();
    }

    public void addPark(ParkEntity park) {
        this.parks.add(park);
    }
}
