package com.fredlecoat.backend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.dtos.ParsedNews;
import com.fredlecoat.backend.parsers.NewsParser;

@Service
public class NewsParsingService {

    @Autowired
    private List<NewsParser> parsers;

    public ParsedNews parse(String text) {
        return parsers.stream()
            .filter(p -> p.isMatching(text))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Format inconnu : " + text))
            .parse(text);
    }
}
