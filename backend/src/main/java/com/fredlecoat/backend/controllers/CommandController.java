package com.fredlecoat.backend.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fredlecoat.backend.services.CsvExportService;
import com.fredlecoat.backend.services.TPIDataService;

@Controller
@RequestMapping("/api")
public class CommandController {

    @Autowired
    private TPIDataService tpiDataService;

    @Autowired
    private CsvExportService csvExportService;

    @GetMapping("/commands")
    public ResponseEntity<String> getAllCommands() {
        String commands;
        commands = """
                   parks : to fetch all parks\n
                   rides : to fetch all rides\n
                   cities : to fetch all cities\n
                   export : to export all data to csv\n
                   """;
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/parks")
    public ResponseEntity<Void> startFetchingParks() {
        this.tpiDataService.getAllParksData();
        return ResponseEntity.ok(null);
    }


    @GetMapping("/rides")
    public ResponseEntity<Void> startFetchingRides() {
        this.tpiDataService.getAllRidesData();
        return ResponseEntity.ok(null);
    }


    @GetMapping("/cities")
    public ResponseEntity<Void> startFetchingCities() {
        this.tpiDataService.getAllCitiesData();
        return ResponseEntity.ok(null);
    }


    @GetMapping("/export")
    public ResponseEntity<Void> startExportingAllData() {
        try {
            this.csvExportService.exportAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(null);
    }

}
