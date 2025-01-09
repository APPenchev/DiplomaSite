package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.error.DiplomaThesisCreationException;
import com.example.DiplomaSite.error.DiplomaThesisValidationException;
import org.springframework.stereotype.Component;

@Component
public class DiplomaThesisValidator {


    public void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DiplomaThesisValidationException("Title cannot be empty");
        }
        if (title.length() < 2 || title.length() > 100) {
            throw new DiplomaThesisValidationException("Title must be between 2 and 100 characters");
        }
    }

    public void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new DiplomaThesisValidationException("Text cannot be empty");
        }
        if (text.length() < 2 || text.length() > 10000) {
            throw new DiplomaThesisValidationException("Text must be between 2 and 10000 characters");
        }
    }

}
