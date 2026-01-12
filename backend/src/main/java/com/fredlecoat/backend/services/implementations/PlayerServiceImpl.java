package com.fredlecoat.backend.services.implementations;

import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.PlayerDataEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.dtos.MainPlayerRequest;
import com.fredlecoat.backend.entities.mappers.PlayerDataMapper;
import com.fredlecoat.backend.entities.mappers.PlayerMapper;
import com.fredlecoat.backend.repositories.PlayerDataRepository;
import com.fredlecoat.backend.repositories.PlayerRepository;
import com.fredlecoat.backend.services.PlayerService;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerDataRepository playerDataRepository;
    private final PlayerMapper playerMapper;
    private final PlayerDataMapper playerDataMapper;

    public PlayerServiceImpl(
        PlayerRepository playerRepository, 
        PlayerDataRepository playerDataRepository,
        PlayerMapper playerMapper, 
        PlayerDataMapper playerDataMapper
    ) {
        this.playerRepository = playerRepository;
        this.playerDataRepository = playerDataRepository;
        this.playerMapper = playerMapper;
        this.playerDataMapper = playerDataMapper;
    }

    @Override
    public PlayerEntity saveMainPlayer(MainPlayerRequest request) {
        if (request == null) {
            return null;
        }

        PlayerEntity entity = this.playerRepository.findFirstByName(request.name()).orElse(this.playerMapper.toEntity(request));
        PlayerDataEntity playerDataEntity = this.playerDataMapper.toEntity(request);

        entity = this.playerRepository.save(entity);
        playerDataEntity.setPlayer(entity);
        this.playerDataRepository.save(playerDataEntity);
        return entity;
    }

}
