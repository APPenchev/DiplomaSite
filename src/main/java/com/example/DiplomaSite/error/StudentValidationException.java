package com.example.DiplomaSite.error;

public class StudentValidationException extends RuntimeException {
    public StudentValidationException(String message) {
        super(message);
    }
}