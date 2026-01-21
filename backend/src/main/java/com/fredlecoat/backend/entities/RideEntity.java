package com.fredlecoat.backend.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fredlecoat.backend.values.RideType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "ride")
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

    @CreationTimestamp
    private Instant createdOn;

    @UpdateTimestamp
    private Instant updatedOn;

    @ManyToMany(mappedBy = "rides")
    private Set<ParkEntity> parks;

    public RideEntity(
        RideType type,
        int maxCapacityByHour,
        int hype,
        String name,
        String brand,
        Long price,
        Long surface
    ) {
        this.type = type;
        this.maxCapacityByHour = maxCapacityByHour;
        this.hype = hype;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.surface = surface;

        this.parks = new HashSet<>();
    }

    public void addPark(ParkEntity park) {
        this.parks.add(park);
    }
}
