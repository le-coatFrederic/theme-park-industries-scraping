package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.RideEntity;

public interface RideService {
    RideEntity findById(Long id);
    RideEntity findByName(String name);
    RideEntity create(RideEntity entity);
}
