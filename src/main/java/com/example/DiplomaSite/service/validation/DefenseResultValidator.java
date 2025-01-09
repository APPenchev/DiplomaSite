package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.error.DefenseResultValidationException;
import org.springframework.stereotype.Component;

@Component
public class DefenseResultValidator {

    public void validateGrade(Double grade) {
        if (grade < 2 || grade > 6) {
            throw new DefenseResultValidationException("Grade must be between 2 and 6");
        }
    }
}
