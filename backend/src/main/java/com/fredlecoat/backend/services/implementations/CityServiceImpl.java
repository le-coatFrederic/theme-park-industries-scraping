package com.fredlecoat.backend.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.repositories.CityRepository;
import com.fredlecoat.backend.services.CityService;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public CityEntity findByName(String name) {
        if (name == null) {
            return null;
        }
        return this.cityRepository.findByName(name);
    }

    @Override
    public CityEntity save(CityEntity entity) {
        if (entity == null || entity.getName() == null) {
            return null;
        }

        CityEntity existing = this.cityRepository.findByName(entity.getName());
        if (existing != null) {
            mergeIntoExisting(existing, entity);
            return this.cityRepository.save(existing);
        }

        return this.cityRepository.save(entity);
    }

    private void mergeIntoExisting(CityEntity existing, CityEntity source) {
        if (source.getDifficulty() != null) {
            existing.setDifficulty(source.getDifficulty());
        }
        if (source.getCountry() != null) {
            existing.setCountry(source.getCountry());
        }
        if (source.getPopulation() != null) {
            existing.setPopulation(source.getPopulation());
        }
        if (source.getAvailableSurface() != null) {
            existing.setAvailableSurface(source.getAvailableSurface());
        }
        if (source.getSurface() != null) {
            existing.setSurface(source.getSurface());
        }
        existing.setMaxHeight(source.getMaxHeight());
        existing.setParkPopulation(source.getParkPopulation());
        existing.setParkCapacity(source.getParkCapacity());
        existing.setPriceByMeter(source.getPriceByMeter());
    }
}
