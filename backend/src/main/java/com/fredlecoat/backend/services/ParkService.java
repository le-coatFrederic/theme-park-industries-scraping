package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.dtos.ParsedNews;

public interface ParkService {
    ParkEntity save(ParkEntity entity);
    ParkEntity findByName(String name);
    ParkEntity findByExternalId(Integer externalId);
    ParkEntity handleParser(ParsedNews news);
    ParkEntity addRide(ParkEntity park, String rideName);
    ParkEntity addRideByImageUrl(ParkEntity park, String imageUrl);
    ParkEntity updateOwnerAndCity(String parkName, PlayerEntity owner, CityEntity city);
}
