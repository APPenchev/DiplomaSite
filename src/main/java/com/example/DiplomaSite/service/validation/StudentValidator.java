package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.error.StudentValidationException;
import org.springframework.stereotype.Component;

@Component
public class StudentValidator {


    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new StudentValidationException("Name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new StudentValidationException("Name must be between 2 and 100 characters");
        }
    }

    public void validateFacultyNumber(String facultyNumber) {
        if (facultyNumber == null || !facultyNumber.matches("^[A-Z]{2}\\d{6}$")) {
            throw new StudentValidationException("Invalid faculty number format. Must be 2 uppercase letters followed by 6 digits.");
        }
    }
}