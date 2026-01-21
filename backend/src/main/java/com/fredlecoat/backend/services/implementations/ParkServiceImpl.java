package com.fredlecoat.backend.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.repositories.ParkRepository;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.values.DashboardActivityType;

@Service
public class ParkServiceImpl implements ParkService {

    @Autowired
    private ParkRepository parkRepository;

    @Autowired
    private RideService rideService;

    @Override
    public ParkEntity create(ParkEntity entity) {
        if (entity == null || entity.getName() == null || entity.getExternalId() == null) {
            return null;
        }
        ParkEntity foundEntity = this.parkRepository.findByName(entity.getName());
        if (foundEntity != null) {
            return foundEntity;
        }
        return parkRepository.save(entity);
    }

    @Override
    public ParkEntity save(ParkEntity entity) {
        if (entity == null) {
            return null;
        }
        return this.parkRepository.save(entity);
    }

    @Override
    public ParkEntity findByName(String name) {
        if (name == null) {
            return null;
        }
        return this.parkRepository.findByName(name);
    }

    @Override
    public ParkEntity findByExternalId(Integer externalId) {
        if (externalId == null) {
            return null;
        }
        return this.parkRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional
    public ParkEntity handleParser(ParsedNews news) {
        if (news.type() == DashboardActivityType.BUYING_RIDE) {
            ParkEntity entity = this.parkRepository.findByName(news.actorParkName());
            return addRide(entity, news.rideName());
        }
        return null;
    }

    @Override
    @Transactional
    public ParkEntity addRide(ParkEntity park, String rideName) {
        if (park == null) {
            return null;
        }

        park.addRide(this.rideService.findByName(rideName));
        return this.parkRepository.save(park);
    }

    @Override
    @Transactional
    public ParkEntity addRideByImageUrl(ParkEntity park, String imageUrl) {
        if (park == null || imageUrl == null) {
            return null;
        }

        var ride = this.rideService.findByImageUrl(imageUrl);
        if (ride != null) {
            park.addRide(ride);
            return this.parkRepository.save(park);
        }
        return park;
    }
}
