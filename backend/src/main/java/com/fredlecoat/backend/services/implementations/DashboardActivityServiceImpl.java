package com.fredlecoat.backend.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.repositories.DashboardActivityRepository;
import com.fredlecoat.backend.services.DashboardActivityService;

@Service
public class DashboardActivityServiceImpl implements DashboardActivityService {

    @Autowired
    private DashboardActivityRepository repository;

    @Override
    public DashboardActivityEntity create(DashboardActivityEntity entity) {
        return repository.save(entity);
    }

}
