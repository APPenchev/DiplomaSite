package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateUserStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UserCredentials;
import com.example.DiplomaSite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    /**
     * Returns the token for the user
     */
    @Operation(summary = "Get Token",
            description = "Returns the token for the user.")
    @RequestMapping("/token")
    @PostMapping
    public ResponseEntity<String> getToken(@Valid @RequestBody UserCredentials userCredentials) {
        String token = userService.getToken(userCredentials);
        return ResponseEntity.ok(token);
    }

    /**
     * Creates a new user that is a teacher
     */
    @Operation(summary = "Create a new Teacher",
            description = "Creates a new Teacher record.")
    @RequestMapping("/create-teacher")

    @PostMapping
    public ResponseEntity<StudentDto> createTeacher(
            @Valid @RequestBody CreateUserStudentDto createUserStudentDto) {
        StudentDto createdStudent = userService.createStudent(createUserStudentDto);
        return ResponseEntity.ok(createdStudent);
    }

    /**
     * Creates a new user that is a student
     */
    @Operation(summary = "Create a new Student",
            description = "Creates a new Student record.")
    @RequestMapping("/create-student")

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(
            @Valid @RequestBody CreateUserStudentDto createUserStudentDto) {
        StudentDto createdStudent = userService.createStudent(createUserStudentDto);
        return ResponseEntity.ok(createdStudent);
    }




}
