package com.fredlecoat.backend.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.values.DashboardActivityType;

@Component
public class BuyLandParser implements NewsParser {

    private static final Pattern PATTERN = Pattern.compile(
        "(.+?) viens d'acheter (\\d+)m² de terrain à (.+?) pour",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isMatching(String text) {
        return PATTERN.matcher(text).matches();
    }

    @Override
    public ParsedNews parse(String text) {
        Matcher matcher = PATTERN.matcher(text);
        matcher.matches();
        return new ParsedNews(
            matcher.group(1),
            matcher.group(3),
            Integer.parseInt(matcher.group(2)),
            null,
            DashboardActivityType.BUYING_LAND
        );
    }

}
