package com.fredlecoat.backend.configuration;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration constants for scraping operations.
 * Centralizes all timing and limit values to avoid magic numbers.
 */
@Configuration
public class ScrapingConfig {

    // Page paths (relative to base URL)
    public static final String WORLD_MAP_PAGE = "game/carte_du_monde.php";
    public static final String PARK_PAGE_TEMPLATE = "game/park/fake/monpark.php?id=";
    public static final String ATTRACTIONS_PAGE = "game/park/attractions.php";

    // Delays for page loading (in milliseconds)
    public static final int PAGE_LOAD_DELAY_MS = 500;
    public static final int COUNTRY_LOAD_DELAY_MS = 500;
    public static final int CITY_LOAD_DELAY_MS = 700;
    public static final int MODAL_LOAD_DELAY_MS = 300;

    // Delays between requests to avoid overwhelming the server
    public static final int DELAY_BETWEEN_PARKS_MS = 200;
    public static final int DELAY_BETWEEN_RIDES_MS = 200;

    // Scraping limits
    public static final int MAX_CONSECUTIVE_ERRORS = 500;
    public static final int MAX_PARK_AMOUNT = 5000;

    // CSS Selectors
    public static final class Selectors {
        // World map page
        public static final String COUNTRY_SELECT = "countrySelect";
        public static final String CITY_SELECT = "citySelect";

        // Park page
        public static final String PARK_HERO = ".park-hero";
        public static final String PARK_TITLE = ".park-hero__title";
        public static final String PARK_LOCATION = ".park-hero__location";
        public static final String PARK_STATS_CARD = ".park-stats-section__card";
        public static final String PARK_STATS_TITLE = ".park-stats-section__card-title";
        public static final String PARK_STATS_VALUE = ".park-stats-section__card-value";
        public static final String ATTRACTION_CARD = ".park-attraction-card";
        public static final String ATTRACTION_IMAGE = ".park-attraction-card__image img";

        // World map parks
        public static final String WORLD_MAP_PARK_ITEM = ".world-map-parc-item";
        public static final String WORLD_MAP_PARK_NAME = ".world-map-parc-name";
        public static final String WORLD_MAP_PARK_CREATOR = ".world-map-parc-creator strong";

        // Attractions store
        public static final String OPEN_STORE_BUTTON = "button#open-attraction-store-btn";
        public static final String STORE_DIALOG = "dialog#attraction-store";
        public static final String STORE_ATTRACTION_ITEM = ".attraction-store__grid-item";

        private Selectors() {}
    }

    // Stats card labels (French)
    public static final class StatsLabels {
        public static final String CAPITAL = "Trésorerie";
        public static final String SOCIAL_CAPITAL = "Capital social";
        public static final String YESTERDAY_VISITORS = "Visiteurs hier";
        public static final String USED_SURFACE = "Surface utilisée";
        public static final String NOTE = "Note";

        private StatsLabels() {}
    }

    // Difficulty translations
    public static final class DifficultyLabels {
        public static final String EASY = "Facile";
        public static final String MEDIUM = "Modéré";
        public static final String HARD = "Difficile";

        private DifficultyLabels() {}
    }
}
