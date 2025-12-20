package com.fredlecoat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.RideEntity;

@Repository
public interface RideRepository extends JpaRepository<RideEntity, Long>{

}
