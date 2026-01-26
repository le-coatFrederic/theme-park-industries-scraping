package com.fredlecoat.backend.entities;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
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

    @Column(unique = true, nullable = false)
    private Integer externalId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity owner;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @Column(nullable = false)
    private Long capital;

    @Column(nullable = false)
    private Long socialCapital;

    private Integer yesterdayVisitors;

    @Column(nullable = false)
    private Integer usedSurface;

    @Column(nullable = false)
    private Integer note;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedOn;

    @ManyToMany
    @JoinTable(
        name = "parks_rides",
        joinColumns = @JoinColumn(name = "park_id"),
        inverseJoinColumns = @JoinColumn(name = "ride_id")
    )
    private Set<RideEntity> rides;

    

    public ParkEntity(Integer externalId, String name, CityEntity city, Long capital, Long socialCapital,
            Integer usedSurface, Integer note) {
        this.externalId = externalId;
        this.name = name;
        this.city = city;
        this.capital = capital;
        this.socialCapital = socialCapital;
        this.usedSurface = usedSurface;
        this.note = note;
    }

    public ParkEntity(Integer externalId, String name, CityEntity city, Long capital, Long socialCapital,
            Integer yesterdayVisitors, Integer usedSurface, Integer note) {
        this.externalId = externalId;
        this.name = name;
        this.city = city;
        this.capital = capital;
        this.socialCapital = socialCapital;
        this.yesterdayVisitors = yesterdayVisitors;
        this.usedSurface = usedSurface;
        this.note = note;
    }



    public void addRide(RideEntity ride) {
        this.rides.add(ride);
        ride.addPark(this);
    }
}
