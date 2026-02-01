package com.fredlecoat.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.ParkEntity;

@Repository
public interface ParkRepository extends JpaRepository<ParkEntity, Long>{
    ParkEntity findByName(String name);
    ParkEntity findByExternalId(Integer externalId);

    @Query("SELECT p.externalId FROM ParkEntity p ORDER BY p.externalId")
    List<Integer> findAllExternalIds();

    @Query("SELECT MAX(p.externalId) FROM ParkEntity p")
    Integer findMaxExternalId();
}
