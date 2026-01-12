package com.fredlecoat.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fredlecoat.backend.entities.PlayerEntity;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long>{
    List<PlayerEntity> findByName(String name);
    Optional<PlayerEntity> findFirstByName(String name);
}
