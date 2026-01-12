package com.fredlecoat.backend.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.parsers.impl.BuyLandParser;
import com.fredlecoat.backend.parsers.impl.BuyRideParser;

@Configuration
public class NewsParserConfig {
    private final List<NewsParser> parsers;

    public NewsParserConfig(
        BuyLandParser buyLandParser,
        BuyRideParser buyRideParser
    ) {
        this.parsers = new ArrayList<>();
        this.parsers.add(buyLandParser);
        this.parsers.add(buyRideParser);
    }

    @Bean
    public List<NewsParser> getParsersList() {
        return this.parsers;
    }
}
