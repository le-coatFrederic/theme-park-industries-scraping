package com.fredlecoat.backend.services.implementations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.repositories.CityRepository;
import com.fredlecoat.backend.repositories.DashboardActivityRepository;
import com.fredlecoat.backend.repositories.ParkRepository;
import com.fredlecoat.backend.repositories.PlayerRepository;
import com.fredlecoat.backend.repositories.RideRepository;
import com.fredlecoat.backend.services.CsvExportService;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    private static final String DELIMITER = ";";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ParkRepository parkRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private DashboardActivityRepository dashboardActivityRepository;

    private String exportDirectory = "/exports";

    @Override
    public void exportAll() throws IOException {
        exportAll(Paths.get(exportDirectory));
    }

    @Override
    public void exportAll(Path directory) throws IOException {
        Files.createDirectories(directory);

        exportParks(directory.resolve("parks.csv"));
        exportRides(directory.resolve("rides.csv"));
        exportCities(directory.resolve("cities.csv"));
        exportPlayers(directory.resolve("players.csv"));
        exportDashboardActivities(directory.resolve("dashboard_activities.csv"));
        exportParksRides(directory.resolve("parks_rides.csv"));

        System.out.println("Export CSV termine dans: " + directory.toAbsolutePath());
    }

    @Override
    public void exportParks(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id;external_id;name;owner_id;owner_name;city_id;city_name;created_on;updated_on");
            writer.newLine();

            parkRepository.findAll().forEach(park -> {
                try {
                    writer.write(String.join(DELIMITER,
                        String.valueOf(park.getId()),
                        park.getExternalId() != null ? String.valueOf(park.getExternalId()) : "",
                        escapeCsv(park.getName()),
                        park.getOwner() != null ? String.valueOf(park.getOwner().getId()) : "",
                        park.getOwner() != null ? escapeCsv(park.getOwner().getName()) : "",
                        park.getCity() != null ? String.valueOf(park.getCity().getId()) : "",
                        park.getCity() != null ? escapeCsv(park.getCity().getName()) : "",
                        park.getCreatedOn() != null ? park.getCreatedOn().toString() : "",
                        park.getUpdatedOn() != null ? park.getUpdatedOn().toString() : ""
                    ));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur export park: " + park.getId(), e);
                }
            });
        }
        System.out.println("Export parks: " + filePath);
    }

    @Override
    public void exportRides(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id;type;name;brand;price;surface;hype;max_capacity_by_hour;image_url;created_on;updated_on");
            writer.newLine();

            rideRepository.findAll().forEach(ride -> {
                try {
                    writer.write(String.join(DELIMITER,
                        String.valueOf(ride.getId()),
                        ride.getType() != null ? ride.getType().name() : "",
                        escapeCsv(ride.getName()),
                        escapeCsv(ride.getBrand()),
                        ride.getPrice() != null ? String.valueOf(ride.getPrice()) : "",
                        ride.getSurface() != null ? String.valueOf(ride.getSurface()) : "",
                        String.valueOf(ride.getHype()),
                        String.valueOf(ride.getMaxCapacityByHour()),
                        escapeCsv(ride.getImageUrl()),
                        ride.getCreatedOn() != null ? ride.getCreatedOn().toString() : "",
                        ride.getUpdatedOn() != null ? ride.getUpdatedOn().toString() : ""
                    ));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur export ride: " + ride.getId(), e);
                }
            });
        }
        System.out.println("Export rides: " + filePath);
    }

    @Override
    public void exportCities(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id;name;country;difficulty;population;available_surface;surface;max_height;park_population;park_capacity;price_by_meter;created_on;updated_on");
            writer.newLine();

            cityRepository.findAll().forEach(city -> {
                try {
                    writer.write(String.join(DELIMITER,
                        String.valueOf(city.getId()),
                        escapeCsv(city.getName()),
                        escapeCsv(city.getCountry()),
                        city.getDifficulty() != null ? city.getDifficulty().name() : "",
                        city.getPopulation() != null ? String.valueOf(city.getPopulation()) : "",
                        city.getAvailableSurface() != null ? String.valueOf(city.getAvailableSurface()) : "",
                        city.getSurface() != null ? String.valueOf(city.getSurface()) : "",
                        String.valueOf(city.getMaxHeight()),
                        String.valueOf(city.getParkPopulation()),
                        String.valueOf(city.getParkCapacity()),
                        String.valueOf(city.getPriceByMeter()),
                        city.getCreatedOn() != null ? city.getCreatedOn().toString() : "",
                        city.getUpdatedOn() != null ? city.getUpdatedOn().toString() : ""
                    ));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur export city: " + city.getId(), e);
                }
            });
        }
        System.out.println("Export cities: " + filePath);
    }

    @Override
    public void exportPlayers(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id;name;created_on;updated_on");
            writer.newLine();

            playerRepository.findAll().forEach(player -> {
                try {
                    writer.write(String.join(DELIMITER,
                        String.valueOf(player.getId()),
                        escapeCsv(player.getName()),
                        player.getCreatedOn() != null ? player.getCreatedOn().toString() : "",
                        player.getUpdatedOn() != null ? player.getUpdatedOn().toString() : ""
                    ));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur export player: " + player.getId(), e);
                }
            });
        }
        System.out.println("Export players: " + filePath);
    }

    @Override
    public void exportDashboardActivities(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id;category;type;posted;text;player_id;city_id;actor_park_id;victim_park_id;ride_id;amount;created_on;updated_on");
            writer.newLine();

            dashboardActivityRepository.findAll().forEach(activity -> {
                try {
                    writer.write(String.join(DELIMITER,
                        String.valueOf(activity.getId()),
                        activity.getCategory() != null ? activity.getCategory().name() : "",
                        activity.getType() != null ? activity.getType().name() : "",
                        activity.getPosted() != null ? activity.getPosted().format(DATE_FORMATTER) : "",
                        escapeCsv(activity.getText()),
                        activity.getPlayer() != null ? String.valueOf(activity.getPlayer().getId()) : "",
                        activity.getCity() != null ? String.valueOf(activity.getCity().getId()) : "",
                        activity.getActorPark() != null ? String.valueOf(activity.getActorPark().getId()) : "",
                        activity.getVictimPark() != null ? String.valueOf(activity.getVictimPark().getId()) : "",
                        activity.getRide() != null ? String.valueOf(activity.getRide().getId()) : "",
                        activity.getAmount() != null ? String.valueOf(activity.getAmount()) : "",
                        activity.getCreatedOn() != null ? activity.getCreatedOn().toString() : "",
                        activity.getUpdatedOn() != null ? activity.getUpdatedOn().toString() : ""
                    ));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur export activity: " + activity.getId(), e);
                }
            });
        }
        System.out.println("Export dashboard activities: " + filePath);
    }

    @Override
    public void exportParksRides(Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("park_id;park_name;ride_id;ride_name");
            writer.newLine();

            parkRepository.findAll().forEach(park -> {
                if (park.getRides() != null) {
                    park.getRides().forEach(ride -> {
                        try {
                            writer.write(String.join(DELIMITER,
                                String.valueOf(park.getId()),
                                escapeCsv(park.getName()),
                                String.valueOf(ride.getId()),
                                escapeCsv(ride.getName())
                            ));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException("Erreur export park_ride: " + park.getId() + "-" + ride.getId(), e);
                        }
                    });
                }
            });
        }
        System.out.println("Export parks_rides: " + filePath);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(DELIMITER) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
