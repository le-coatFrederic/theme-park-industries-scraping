package com.fredlecoat.backend.services.implementations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    public DashboardActivityEntity create(WebElement element) {
        try {
            DashboardActivityCategory category = DashboardActivityCategory.PARK;

            // Extract date safely
            String dateText = element.findElement(By.cssSelector(".news-journal__date")).getText();
            LocalDateTime date = parseActivityDate(dateText);

            // Extract text safely
            String text = element.findElement(By.className("news-journal__text")).getText();

            System.out.println("Parsing activity: " + text.substring(0, Math.min(50, text.length())));

            // Parse text to determine activity type
            ParsedNews parsedNews = parsingService.parse(text);
            DashboardActivityType type = parsedNews.type();

            // Handle parsed news (e.g., add rides to parks for BUYING_RIDE activities)
            this.parkService.handleParser(parsedNews);

            // Check if activity already exists with same date, type, category, and text
            var existingActivity = this.repository.findByPostedAndTypeAndCategoryAndText(date, type, category, text);
            if (existingActivity.isPresent()) {
                System.out.println("Activity already exists: " + text.substring(0, Math.min(50, text.length())));
                return existingActivity.get();
            }

            // Fetch or create entities from parsed news
            PlayerEntity player = this.playerService.findByName(parsedNews.playerName());
            CityEntity city = this.cityService.findByName(parsedNews.cityName());
            ParkEntity actorPark = this.parkService.findByName(parsedNews.actorParkName());
            ParkEntity victimPark = this.parkService.findByName(parsedNews.victimParkName());
            RideEntity ride = this.rideService.findByName(parsedNews.rideName());

            // Long amount from parsed news
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

    /**
     * Converts French formatted date string to LocalDateTime
     * @param dateString formatted as "16/01/2026 à 14:26"
     * @return LocalDateTime object
     */
    public LocalDateTime parseActivityDate(String dateString) {
        return LocalDateTime.parse(dateString, DATE_FORMATTER);
    }

}
