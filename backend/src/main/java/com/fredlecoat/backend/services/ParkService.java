package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.dtos.ParsedNews;

public interface ParkService {
    ParkEntity create(ParkEntity entity);
    ParkEntity findByName(String name);
    ParkEntity handleParser(ParsedNews news);
    ParkEntity addRide(ParkEntity park,String rideName);
}
