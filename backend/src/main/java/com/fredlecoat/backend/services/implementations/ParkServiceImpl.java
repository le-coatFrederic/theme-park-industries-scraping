package com.fredlecoat.backend.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
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
    public ParkEntity save(ParkEntity entity) {
        if (entity == null || entity.getName() == null) {
            return null;
        }

        // Si l'entité n'a pas d'ID, chercher par externalId ou par nom
        if (entity.getId() == null) {
            ParkEntity existing = null;
            if (entity.getExternalId() != null) {
                existing = this.parkRepository.findByExternalId(entity.getExternalId());
            }
            if (existing == null) {
                existing = this.parkRepository.findByName(entity.getName());
            }
            if (existing != null) {
                return existing;
            }
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
        if (park == null || park.getId() == null || imageUrl == null) {
            return null;
        }

        // Reload park within this transaction to avoid lazy loading issues
        ParkEntity managedPark = this.parkRepository.findById(park.getId()).orElse(null);
        if (managedPark == null) {
            return null;
        }

        var ride = this.rideService.findByImageUrl(imageUrl);
        if (ride != null) {
            managedPark.addRide(ride);
            return this.parkRepository.save(managedPark);
        }
        return managedPark;
    }

    @Override
    @Transactional
    public ParkEntity updateOwnerAndCity(String parkName, PlayerEntity owner, CityEntity city) {
        if (parkName == null) {
            return null;
        }

        ParkEntity park = this.parkRepository.findByName(parkName);
        if (park == null) {
            System.out.println("    Parc non trouvé: " + parkName);
            return null;
        }

        park.setOwner(owner);
        park.setCity(city);
        System.out.println("    Parc mis à jour: " + parkName + " - Owner: " + (owner != null ? owner.getName() : "null") + " - City: " + (city != null ? city.getName() : "null"));
        return this.parkRepository.save(park);
    }
}
