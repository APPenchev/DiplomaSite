package com.example.DiplomaSite.service.validation;

import com.example.DiplomaSite.error.DiplomaAssignmentValidationException;
import org.springframework.stereotype.Service;

@Service
public class DiplomaAssignmentValidator {

    public void validateTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new DiplomaAssignmentValidationException("Topic cannot be empty");
        }
        if (topic.length() < 2 || topic.length() > 100) {
            throw new DiplomaAssignmentValidationException("Topic must be between 2 and 100 characters");
        }
    }

    public void validateGoal(String goal) {
        if (goal == null || goal.trim().isEmpty()) {
            throw new DiplomaAssignmentValidationException("Goal cannot be empty");
        }
        if (goal.length() < 2 || goal.length() > 10000) {
            throw new DiplomaAssignmentValidationException("Goal must be between 2 and 10000 characters");
        }
    }

    public void validateTasks(String tasks) {
        if (tasks == null || tasks.trim().isEmpty()) {
            throw new DiplomaAssignmentValidationException("Tasks cannot be empty");
        }
        if (tasks.length() < 2 || tasks.length() > 10000) {
            throw new DiplomaAssignmentValidationException("Tasks must be between 2 and 10000 characters");
        }
    }

    public void validateTechnologies(String technologies) {
        if (technologies == null || technologies.trim().isEmpty()) {
            throw new DiplomaAssignmentValidationException("Technologies cannot be empty");
        }
        if (technologies.length() < 2 || technologies.length() > 10000) {
            throw new DiplomaAssignmentValidationException("Technologies must be between 2 and 10000 characters");
        }
    }
}
