package com.example.DiplomaSite.error;

public class StudentCreationException extends RuntimeException {
    public StudentCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}