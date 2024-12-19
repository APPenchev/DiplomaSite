package com.example.DiplomaSite.error;

public class StudentDeletionException extends RuntimeException {
    public StudentDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
