package com.fredlecoat.backend.services.implementations;

import org.springframework.stereotype.Service;

import com.fredlecoat.backend.services.TextToDataConverterService;

@Service
public class TextToDataConverterServiceImpl implements TextToDataConverterService {

    @Override
    public String convert(String key, String value) {
        if (key.equals("money")) {
            return moneyConvertion(value);
        } 
        
        if (key.equals("level")) {
            return levelConvertion(value);
        } 
        
        if (key.equals("experience")) {
            return experienceConvertion(value);
        }

        return "";
    }

    private String moneyConvertion(String moneyText) {
        return moneyText
                .trim()
                .substring(0, moneyText.length() - 5)
                .replace(" ", "");
    }

    private String levelConvertion(String levelText) {
        return levelText
                .trim();
    }

    private String experienceConvertion(String experienceText) {
        int separatorIndex = experienceText.indexOf('/');
        return experienceText
                .trim()
                .substring(0, separatorIndex)
                .replace(" ", "");
    }

}
