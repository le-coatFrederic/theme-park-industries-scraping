package com.fredlecoat.backend.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.services.ScraperService;

@Component
public class RequestScheduler {
    @Autowired
    private ScraperService scraperService;

    @Scheduled(fixedRate = 1000 * 60 * 30) // 30 minutes
    public void mediumScheduler() {
        //System.out.println("########## PERSONAL DATA ##########");
        //this.scraperService.getPersonalData();
    }

    @Scheduled(fixedRate = 1000 * 60) // 1 minute
    public void lowScheduler() {
        //System.out.println("########## PERSONAL DATA ##########");
        this.scraperService.getDashboardActivities();
    }
}
