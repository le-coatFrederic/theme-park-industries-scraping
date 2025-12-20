package com.fredlecoat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.DashboardActivityEntity;

@Repository
public interface DashboardActivityRepository extends JpaRepository<DashboardActivityEntity, Long>{

}
