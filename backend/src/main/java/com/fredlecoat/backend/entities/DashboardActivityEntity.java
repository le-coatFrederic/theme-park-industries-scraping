package com.fredlecoat.backend.entities;

import java.time.LocalDateTime;

import com.fredlecoat.backend.values.DashboardActivityCategory;
import com.fredlecoat.backend.values.DashboardActivityType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
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

    public DashboardActivityEntity(DashboardActivityCategory category, LocalDateTime posted, DashboardActivityType type, String text) {
        this.category = category;
        this.posted = posted;
        this.type = type;
        this.text = text;
    }
}
