package com.fredlecoat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.ParkEntity;

@Repository
public interface ParkRepository extends JpaRepository<ParkEntity, Long>{
    ParkEntity findByName(String name);
    ParkEntity findByExternalId(Integer externalId);
}
