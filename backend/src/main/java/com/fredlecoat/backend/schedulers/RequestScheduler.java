package com.fredlecoat.backend.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.services.CsvExportService;
import com.fredlecoat.backend.services.ScraperService;

@Component
public class RequestScheduler {
    @Autowired
    private ScraperService scraperService;

    @Autowired 
    private CsvExportService csvExportService;

    @Scheduled(cron = "0 0 * * * ?", zone = "Europe/Paris") // Tous les jours
    public void daily() {
        this.scraperService.getAllRidesData();
        try {
            this.csvExportService.exportAll();
        } catch (Exception e) {
            System.err.println("Erreur export CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 */6 * * ?", zone = "Europe/Paris") // Toutes les six heures
    public void longScheduler() {
        this.scraperService.getAllParksData();
    }

    @Scheduled(fixedRate = 1000 * 60 * 30) // 30 minutes
    public void mediumScheduler() {
        this.scraperService.getAllCitiesData();

        // EXPORT DE LA DATA EN CSV
        try {
            this.csvExportService.exportAll();
        } catch (Exception e) {
            System.err.println("Erreur export CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 1000 * 60) // 1 minute
    public void lowScheduler() {
        //System.out.println("########## PERSONAL DATA ##########");
        this.scraperService.getDashboardActivities();
    }
}
