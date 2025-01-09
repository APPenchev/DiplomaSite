package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.enums.TeacherPosition;
import com.example.DiplomaSite.error.TeacherValidationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TeacherValidator {


    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new TeacherValidationException("Name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new TeacherValidationException("Name must be between 2 and 100 characters");
        }
    }

}
