package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.CityEntity;

public interface CityService {
    CityEntity findByName(String name);
    CityEntity create(CityEntity entity);
}
