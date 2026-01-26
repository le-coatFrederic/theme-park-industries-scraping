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

        CityEntity foundEntity = this.cityRepository.findByName(entity.getName());
        if (foundEntity != null) {
            return foundEntity;
        }

        return this.cityRepository.save(entity);
    }
}
