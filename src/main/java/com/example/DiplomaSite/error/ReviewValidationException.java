package com.example.DiplomaSite.error;

public class ReviewValidationException extends RuntimeException{
    public ReviewValidationException(String message) {
        super(message);
    }
}
