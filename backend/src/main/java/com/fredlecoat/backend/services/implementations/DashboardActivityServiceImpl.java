package com.fredlecoat.backend.services.implementations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fredlecoat.backend.entities.CityEntity;
import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.entities.ParkEntity;
import com.fredlecoat.backend.entities.PlayerEntity;
import com.fredlecoat.backend.entities.RideEntity;
import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.repositories.DashboardActivityRepository;
import com.fredlecoat.backend.services.CityService;
import com.fredlecoat.backend.services.DashboardActivityService;
import com.fredlecoat.backend.services.ParkService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.services.RideService;
import com.fredlecoat.backend.values.DashboardActivityCategory;
import com.fredlecoat.backend.values.DashboardActivityType;

@Service
public class DashboardActivityServiceImpl implements DashboardActivityService {

    @Autowired
    private DashboardActivityRepository repository;

    @Autowired
    private NewsParsingService parsingService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CityService cityService;

    @Autowired
    private RideService rideService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    @Override
    @Transactional
    public DashboardActivityEntity create(Map<String, Object> activityData) {
        try {
            DashboardActivityCategory category = DashboardActivityCategory.PARK;

            String dateText = activityData.get("date").toString();
            LocalDateTime date = parseActivityDate(dateText);

            String text = activityData.get("text").toString();

            System.out.println("Parsing activity: " + text.substring(0, Math.min(50, text.length())));

            ParsedNews parsedNews = parsingService.parse(text);
            DashboardActivityType type = parsedNews.type();

            this.parkService.handleParser(parsedNews);

            var existingActivity = this.repository.findByPostedAndTypeAndCategoryAndText(date, type, category, text);
            if (existingActivity.isPresent()) {
                System.out.println("Activity already exists: " + text.substring(0, Math.min(50, text.length())));
                return existingActivity.get();
            }

            PlayerEntity player = this.playerService.findByName(parsedNews.playerName());
            CityEntity city = this.cityService.findByName(parsedNews.cityName());
            ParkEntity actorPark = this.parkService.findByName(parsedNews.actorParkName());
            ParkEntity victimPark = this.parkService.findByName(parsedNews.victimParkName());
            RideEntity ride = this.rideService.findByName(parsedNews.rideName());

            Long amount = parsedNews.amount() != null ? parsedNews.amount().longValue() : null;

            DashboardActivityEntity newActivity = new DashboardActivityEntity(
                category,
                date,
                type,
                text,
                player,
                city,
                actorPark,
                victimPark,
                ride,
                amount
            );

            DashboardActivityEntity entity = this.repository.save(newActivity);
            System.out.println("Activity saved: " + entity.toString());
            return entity;

        } catch (Exception e) {
            System.err.println("Error creating dashboard activity: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create dashboard activity", e);
        }
    }

    public LocalDateTime parseActivityDate(String dateString) {
        return LocalDateTime.parse(dateString, DATE_FORMATTER);
    }
}
