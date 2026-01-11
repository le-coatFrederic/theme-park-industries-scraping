package com.fredlecoat.backend.services;

import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.requests.MainPlayerRequest;

public interface PlayerService {
    PlayerEntity saveMainPlayer(MainPlayerRequest request);
}
