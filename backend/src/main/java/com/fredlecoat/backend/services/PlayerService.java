package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.dtos.MainPlayerRequest;

public interface PlayerService {
    PlayerEntity saveMainPlayer(MainPlayerRequest request);
    PlayerEntity findByName(String name);
    PlayerEntity findOrCreate(String name);
}
