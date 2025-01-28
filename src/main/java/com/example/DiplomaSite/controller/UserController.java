package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.*;
import com.example.DiplomaSite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

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

    @PostMapping("/token")
        public ResponseEntity<Token> getToken(@Valid @RequestBody UserCredentials userCredentials) {
        Token token = userService.getToken(userCredentials);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/identify")
    public String identify(Authentication auth) {
        String userId = KeycloakUtils.getUserId(auth);
        Set<String> roles = KeycloakUtils.getUserRoles(auth);
        return "User ID: " + userId + ", Roles: " + roles;
    }

    /**
     * Creates a new user that is a teacher
     */
    @Operation(summary = "Create a new Teacher",
            description = "Creates a new Teacher record.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/create-teacher")
    public ResponseEntity<TeacherDto> createTeacher(
            @Valid @RequestBody CreateUserTeacherDto createUserTeacherDto) {
        TeacherDto createdStudent = userService.createTeacher(createUserTeacherDto);
        return ResponseEntity.ok(createdStudent);
    }

    /**
     * Creates a new user that is a student
     */
    @Operation(summary = "Create a new Student",
            description = "Creates a new Student record.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/create-student")
    public ResponseEntity<StudentDto> createStudent(
            @Valid @RequestBody CreateUserStudentDto createUserStudentDto) {
        StudentDto createdStudent = userService.createStudent(createUserStudentDto);
        return ResponseEntity.ok(createdStudent);
    }

    /**
     * Deletes the user with the specified Keycloak ID
     */

    @Operation(summary = "Delete User",
            description = "Deletes the user with the specified Keycloak ID.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping("/{keycloakId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String keycloakId) {
        boolean deleted = userService.deleteUser(keycloakId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @Operation(summary = "Get JWT Claims",
            description = "Returns the claims from the JWT token.")
    @RequestMapping("/claims")
    @GetMapping
    public Map<String, Object> getJwtClaims(@AuthenticationPrincipal Jwt principal) {
        return principal.getClaims();
    }



}
