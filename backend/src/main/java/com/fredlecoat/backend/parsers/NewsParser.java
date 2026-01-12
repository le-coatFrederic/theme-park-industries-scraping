package com.fredlecoat.backend.parsers;

import com.fredlecoat.backend.entities.dtos.ParsedNews;

public interface NewsParser {
    boolean isMatching(String text);
    ParsedNews parse(String text);
}
