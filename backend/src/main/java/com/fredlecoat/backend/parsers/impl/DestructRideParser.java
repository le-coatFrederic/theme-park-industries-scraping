package com.fredlecoat.backend.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.values.DashboardActivityType;

@Component
public class DestructRideParser implements NewsParser {

    private static final Pattern PATTERN = Pattern.compile(
    "(.+?) à (.+?) vient d'annoncer la destruction de (.+?).",
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
            null,
            matcher.group(2),
            matcher.group(1),
            null,
            matcher.group(3),
            null,
            DashboardActivityType.DESTRUCT_RIDE
        );
    }

}
