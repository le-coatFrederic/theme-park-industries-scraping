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
        if (entity == null || entity.getName() == null) {
            return null;
        }
        ParkEntity foundEntity = this.parkRepository.findByName(entity.getName());
        if (foundEntity != null) {
            return foundEntity;
        }
        return parkRepository.save(entity);
    }

    @Override
    public ParkEntity findByName(String name) {
        if (name == null) {
            return null;
        }

        ParkEntity foundEntity = this.parkRepository.findByName(name);
        if (foundEntity != null) {
            return foundEntity;
        }

        ParkEntity newPark = new ParkEntity(null, name, null, null);
        return this.parkRepository.save(newPark);
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

    /**
     * Adds a ride to a park
     * @param rideName name of the ride
     * @param parkName name of the park
     * @param price price of the ride
     * @return updated ParkEntity
     */
    @Override
    @Transactional
    public ParkEntity addRide(ParkEntity park, String rideName) {
        if (park == null) {
            return null;
        }

        park.addRide(this.rideService.findByName(rideName));
        return park;
    }
}
