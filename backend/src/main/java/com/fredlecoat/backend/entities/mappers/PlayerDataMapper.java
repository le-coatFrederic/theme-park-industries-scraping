package com.fredlecoat.backend.entities.mappers;

import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.PlayerDataEntity;
import com.fredlecoat.backend.entities.dtos.MainPlayerRequest;

@Component
public class PlayerDataMapper {
    public PlayerDataEntity toEntity(MainPlayerRequest dto) {
        return new PlayerDataEntity(dto.money(), dto.level(), dto.experience());
    }
}
