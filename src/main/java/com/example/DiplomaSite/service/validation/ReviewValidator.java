package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.error.ReviewValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReviewValidator {

    public void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ReviewValidationException("Text cannot be empty");
        }
        if (text.length() < 2 || text.length() > 10000) {
            throw new ReviewValidationException("Text must be between 2 and 10000 characters");
        }
    }


}
