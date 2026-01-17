package com.fredlecoat.backend.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.parsers.impl.BuyLandParser;
import com.fredlecoat.backend.parsers.impl.BuyParkParser;
import com.fredlecoat.backend.parsers.impl.BuyRideFromPlayer;
import com.fredlecoat.backend.parsers.impl.BuyRideParser;
import com.fredlecoat.backend.parsers.impl.DestructRideParser;
import com.fredlecoat.backend.parsers.impl.SellRideParser;

@Configuration
public class NewsParserConfig {
    private final List<NewsParser> parsers;

    public NewsParserConfig(
        BuyLandParser buyLandParser,
        BuyRideParser buyRideParser,
        BuyParkParser buyParkParser,
        BuyRideFromPlayer buyRideFromPlayer,
        SellRideParser sellRideParser,
        DestructRideParser destructRideParser
    ) {
        this.parsers = new ArrayList<>();
        this.parsers.add(buyLandParser);
        this.parsers.add(buyRideParser);
        this.parsers.add(buyParkParser);
        this.parsers.add(buyRideFromPlayer);
        this.parsers.add(sellRideParser);
        this.parsers.add(destructRideParser);
    }

    @Bean
    public List<NewsParser> getParsersList() {
        return this.parsers;
    }
}
