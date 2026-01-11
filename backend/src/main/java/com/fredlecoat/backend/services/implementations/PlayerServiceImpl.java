package com.fredlecoat.backend.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.mappers.PlayerMapper;
import com.fredlecoat.backend.entities.requests.MainPlayerRequest;
import com.fredlecoat.backend.repositories.PlayerRepository;
import com.fredlecoat.backend.services.PlayerService;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository repository;
    private final PlayerMapper mapper;

    PlayerServiceImpl(
        PlayerRepository repository,
        PlayerMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PlayerEntity saveMainPlayer(MainPlayerRequest request) {
        if (request == null) {
            return null;
        }

        String name = request.name();
        List<PlayerEntity> players = this.repository.findByName(name);
        PlayerEntity entity = players.isEmpty() ? null : players.getFirst();

        if (entity != null) {
            entity.setMoney(request.money());
            entity.setLevel(request.level());
            entity.setExperience(request.experience());
            this.repository.save(entity);
        } else {
            entity = this.mapper.toEntity(request);
        }

        entity = this.repository.save(entity);
        return entity;
    }

}
