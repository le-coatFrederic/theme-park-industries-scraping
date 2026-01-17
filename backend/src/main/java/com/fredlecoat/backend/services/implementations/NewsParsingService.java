package com.fredlecoat.backend.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.parsers.NewsParser;
import com.fredlecoat.backend.values.DashboardActivityType;

@Service
public class NewsParsingService {

    @Autowired
    private List<NewsParser> parsers;

    public ParsedNews parse(String text) {
        return parsers.stream()
            .filter(p -> p.isMatching(text))
            .findFirst()
            .map(parser -> parser.parse(text))
            .orElseGet(() -> createDefaultParsedNews(text));
    }

    /**
     * Creates a default ParsedNews when no parser matches the text
     */
    private ParsedNews createDefaultParsedNews(String text) {
        System.out.println("No matching parser found for text: " + text);
        return new ParsedNews(null, null, null, null, null, null, DashboardActivityType.NONE);
    }
}
