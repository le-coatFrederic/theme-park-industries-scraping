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
        "(.+?) viens d'acheter ([\\d ]+)m² de terrain à (.+?) pour un agrandissement.",
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
        // Remove spaces from quantity (e.g., "2 300" -> "2300")
        String quantityStr = matcher.group(2).replaceAll("\\s+", "");
        return new ParsedNews(
            null,
            matcher.group(3),
            matcher.group(1),
            null,
            null,
            Integer.parseInt(quantityStr),
            DashboardActivityType.BUYING_LAND
        );
    }

}
