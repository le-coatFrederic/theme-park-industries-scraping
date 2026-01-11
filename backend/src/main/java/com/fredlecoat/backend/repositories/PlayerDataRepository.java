package com.fredlecoat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.PlayerDataEntity;

@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerDataEntity, Long> {

}
