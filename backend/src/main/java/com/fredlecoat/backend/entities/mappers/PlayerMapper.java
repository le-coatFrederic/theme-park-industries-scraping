package com.fredlecoat.backend.entities.mappers;

import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.dtos.MainPlayerRequest;

@Component
public class PlayerMapper {
    public PlayerEntity toEntity(MainPlayerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException();
        }

        return new PlayerEntity(
            request.name()
        );
    }
}
