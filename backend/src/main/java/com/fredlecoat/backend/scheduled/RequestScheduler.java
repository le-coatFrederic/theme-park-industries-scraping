package com.fredlecoat.backend.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.services.ScraperService;

@Component
public class RequestScheduler {
    @Autowired
    private ScraperService scraperService;

    @Scheduled(fixedRate = 1000 * 3600 / 2)
    public void mediumScheduler() {
        System.out.println("########## PERSONAL DATA ##########");
        this.scraperService.getPersonalData();
    }
}
