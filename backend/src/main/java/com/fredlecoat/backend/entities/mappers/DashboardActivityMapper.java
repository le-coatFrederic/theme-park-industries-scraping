package com.fredlecoat.backend.entities.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fredlecoat.backend.entities.DashboardActivityEntity;
import com.fredlecoat.backend.entities.dtos.DashboardActivityTextScrapedDtoRequest;
import com.fredlecoat.backend.services.implementations.NewsParsingService;
import com.fredlecoat.backend.values.DashboardActivityCategory;
import com.fredlecoat.backend.values.DashboardActivityType;

@Component
public class DashboardActivityMapper {
    
    @Autowired
    private NewsParsingService parsingService;

    public DashboardActivityEntity toEntity(DashboardActivityTextScrapedDtoRequest dto) {
        return new DashboardActivityEntity(
            this.categoryTranslate(dto.category()),
            this.dateTimeTranslator(dto.date()),
            this.activityTypeTranslator(dto.event()),
            dto.event()
        );
    }

    private DashboardActivityType activityTypeTranslator(String event) {
        return parsingService.parse(event).type();
    }

    private DashboardActivityCategory categoryTranslate(String category) {
        if (category.equals("Parcs")) {
            return DashboardActivityCategory.PARK;
        }

        return null;
    }

    private LocalDateTime dateTimeTranslator(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }
}
