package com.fredlecoat.backend.services;

import java.io.IOException;
import java.nio.file.Path;

public interface CsvExportService {
    void exportAll() throws IOException;
    void exportAll(Path directory) throws IOException;
    void exportParks(Path filePath) throws IOException;
    void exportRides(Path filePath) throws IOException;
    void exportCities(Path filePath) throws IOException;
    void exportPlayers(Path filePath) throws IOException;
    void exportDashboardActivities(Path filePath) throws IOException;
    void exportParksRides(Path filePath) throws IOException;
}
