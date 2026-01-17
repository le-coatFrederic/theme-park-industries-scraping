package com.fredlecoat.backend.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.values.DashboardActivityType;

@Component
public class SellRideParser implements NewsParser {

    private static final Pattern PATTERN = Pattern.compile(
    "(.+?) à (.+?) vient de mettre en (.+?) pour ([\\d ]+) €.",
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
        String quantityStr = matcher.group(4).replaceAll("\\s+", "");
        return new ParsedNews(
            null,
            matcher.group(2),
            matcher.group(1),
            null,
            matcher.group(3),
            Integer.parseInt(quantityStr),
            DashboardActivityType.SELLING_RIDE
        );
    }

}
