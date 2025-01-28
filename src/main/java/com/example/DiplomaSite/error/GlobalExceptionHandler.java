package com.example.DiplomaSite.error;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({
            StudentNotFoundException.class,
            StudentValidationException.class,
            StudentCreationException.class,
            StudentDeletionException.class,
            DefenseResultCreationException.class,
            DefenseResultNotFoundException.class,
            DefenseResultValidationException.class,
            DefenseResultDeletionException.class,
            DiplomaDefenseCreationException.class,
            DiplomaDefenseNotFoundException.class,
            DiplomaDefenseValidationException.class,
            DiplomaDefenseDeletionException.class,
            DiplomaThesisCreationException.class,
            DiplomaThesisNotFoundException.class,
            DiplomaThesisValidationException.class,
            DiplomaThesisDeletionException.class,
            DiplomaAssignmentCreationException.class,
            DiplomaAssignmentNotFoundException.class,
            DiplomaAssignmentValidationException.class,
            DiplomaAssignmentDeletionException.class,
            ReviewCreationException.class,
            ReviewNotFoundException.class,
            ReviewValidationException.class,
            ReviewDeletionException.class,
            TeacherCreationException.class,
            TeacherNotFoundException.class,
            TeacherValidationException.class,
            TeacherDeletionException.class,
    })
    public ResponseEntity<ErrorResponse> handleStudentExceptions(RuntimeException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}