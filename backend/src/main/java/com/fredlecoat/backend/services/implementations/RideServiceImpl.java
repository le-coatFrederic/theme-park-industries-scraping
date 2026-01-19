package com.fredlecoat.backend.services.implementations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.repositories.RideRepository;
import com.fredlecoat.backend.services.RideService;

@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Override
    public RideEntity findById(Long id) {
        if (id == null) {
            return null;
        }

        Optional<RideEntity> foundEntity = this.rideRepository.findById(id);
        if (foundEntity.isPresent()) {
            return foundEntity.get();
        }

        return null;
    }

    @Override
    public RideEntity create(RideEntity entity) {
        if (entity == null) {
            return null;
        }

        RideEntity foundEntity = this.rideRepository.findByNameAndBrand(entity.getName(), entity.getBrand());
        if (foundEntity != null) {
            return foundEntity;
        }

        return this.rideRepository.save(entity);
    }

    @Override
    public RideEntity findByName(String name) {
        if (name == null) {
            return null;
        }

        try {
            String ride[] = name.split(" de ");
            if (ride.length < 2) {
                return null;
            }

            if (ride.length > 2) {
                String merged = "";
                for (int i = 0; i < ride.length - 1; i++) {
                    merged += ride[i];
                }
                ride[0] = merged;
            }

            RideEntity foundEntity = this.rideRepository.findByNameAndBrand(ride[0], ride[1]);
            if (foundEntity != null) {
                return foundEntity;
            }

            // Create and save new ride if not found
            RideEntity newRide = new RideEntity(
                null,  // type
                0,     // maxCapacityByHour
                0,     // hype
                ride[0],  // name
                ride[1],  // brand
                null,  // price
                null  // surface
            );
            return this.rideRepository.save(newRide);
        } catch (Exception e) {
            System.err.println("Error parsing ride name: " + name + " - " + e.getMessage());
            return null;
        }
    }
}
