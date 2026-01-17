package com.fredlecoat.backend.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.values.DashboardActivityCategory;
import com.fredlecoat.backend.values.DashboardActivityType;

@Repository
public interface DashboardActivityRepository extends JpaRepository<DashboardActivityEntity, Long>{

    Optional<DashboardActivityEntity> findByPostedAndTypeAndCategoryAndText(
        LocalDateTime posted,
        DashboardActivityType type,
        DashboardActivityCategory category,
        String text
    );
}
