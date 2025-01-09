package com.example.DiplomaSite.error;

public class ReviewCreationException extends RuntimeException{
    public ReviewCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
