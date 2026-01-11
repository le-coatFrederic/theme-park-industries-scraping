package com.fredlecoat.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;

    @Column(nullable = true)
    private int money;

    @Column(nullable = true)
    private int level;

    @Column(nullable = true)
    private int experience;

    public PlayerEntity(
        String name
    ) {
        this.name = name;
    }

    public PlayerEntity(
        String name, 
        int money, 
        int level, 
        int experience
    ) {
        this.name = name;
        this.money = money;
        this.level = level;
        this.experience = experience;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "PlayerEntity [id=" + id + ", name=" + name + ", money=" + money + ", level=" + level + ", experience="
                + experience + "]";
    }
}
