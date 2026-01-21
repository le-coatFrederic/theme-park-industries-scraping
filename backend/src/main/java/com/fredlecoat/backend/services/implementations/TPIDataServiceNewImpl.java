package com.fredlecoat.backend.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.services.TPIDataService;
import com.fredlecoat.backend.services.scrapers.CityScraper;
import com.fredlecoat.backend.services.scrapers.ParkScraper;
import com.fredlecoat.backend.services.scrapers.RideScraper;

@Service
public class TPIDataServiceNewImpl implements TPIDataService {

    @Autowired
    private RideScraper rideScraper;

    @Autowired
    private CityScraper cityScraper;

    @Autowired
    private ParkScraper parkScraper;

    @Override
    public void getAllRidesData() {
        rideScraper.scrapeAllRides();
    }

    @Override
    public void getAllCitiesData() {
        cityScraper.scrapeAllCities();
    }

    @Override
    public void getAllParksData() {
        parkScraper.scrapeAllParks();
    }
}
