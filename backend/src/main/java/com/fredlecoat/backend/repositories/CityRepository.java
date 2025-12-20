package com.fredlecoat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

}
