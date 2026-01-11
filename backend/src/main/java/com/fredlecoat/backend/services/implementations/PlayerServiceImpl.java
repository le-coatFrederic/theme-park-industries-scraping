package com.fredlecoat.backend.services.implementations;

import java.util.List;

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

        List<PlayerEntity> players = this.playerRepository.findByName(request.name());
        PlayerEntity entity = players.isEmpty() ? null : players.getFirst();

        if (entity != null) {
            PlayerDataEntity playerDataEntity = this.playerDataMapper.toEntity(request);
            playerDataEntity.setPlayer(entity);
            this.playerDataRepository.save(playerDataEntity);
        } else {
            entity = this.playerMapper.toEntity(request);
        }

        entity = this.playerRepository.save(entity);
        return entity;
    }

}
