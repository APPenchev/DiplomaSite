package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateDiplomaDefenseDto;
import com.example.DiplomaSite.dto.DiplomaDefenseDto;
import com.example.DiplomaSite.dto.UpdateDiplomaDefenseDto;
import com.example.DiplomaSite.service.DiplomaDefenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing Diploma Defenses.
 */
@RestController
@RequestMapping("/api/diploma-defense")
@Validated
public class DiplomaDefenseController {

    private final DiplomaDefenseService diplomaDefenseService;

    @Autowired
    public DiplomaDefenseController(DiplomaDefenseService diplomaDefenseService) {
        this.diplomaDefenseService = diplomaDefenseService;
    }

    /**
     * Retrieves all existing DiplomaDefenses.
     */
    @Operation(summary = "Get all DiplomaDefenses",
            description = "Returns a list of all DiplomaDefense records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping
    public ResponseEntity<List<DiplomaDefenseDto>> findAll() {
        List<DiplomaDefenseDto> defenses = diplomaDefenseService.findAll();
        return ResponseEntity.ok(defenses);
    }

    /**
     * Retrieves a single DiplomaDefense by its ID.
     */
    @Operation(summary = "Get DiplomaDefense by ID",
            description = "Returns the DiplomaDefense with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "DiplomaDefense not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/{id}")
    public ResponseEntity<DiplomaDefenseDto> getById(
            @PathVariable @Positive Long id,
            Authentication auth
    ) {
        DiplomaDefenseDto defense = diplomaDefenseService.getById(id, auth);
        return ResponseEntity.ok(defense);
    }

    /**
     * Creates a new DiplomaDefense.
     */
    @Operation(summary = "Create DiplomaDefense",
            description = "Creates a new DiplomaDefense record.")
    @ApiResponse(responseCode = "201", description = "Successfully created record.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PostMapping
    public ResponseEntity<DiplomaDefenseDto> create(
            @Valid @RequestBody CreateDiplomaDefenseDto diplomaDefense,
            Authentication authentication) {
        DiplomaDefenseDto defense = diplomaDefenseService.create(diplomaDefense, authentication);
        return new ResponseEntity<>(defense, HttpStatus.CREATED);
    }

    /**
     * Updates an existing DiplomaDefense.
     */
    @Operation(summary = "Update DiplomaDefense",
            description = "Updates an existing DiplomaDefense record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "DiplomaDefense not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PutMapping("/{id}")
    public ResponseEntity<DiplomaDefenseDto> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateDiplomaDefenseDto diplomaDefense,
            Authentication authentication) {
        DiplomaDefenseDto updatedDefense = diplomaDefenseService.update(id, diplomaDefense, authentication);
        return ResponseEntity.ok(updatedDefense);
    }

    /**
     * Deletes an existing DiplomaDefense.
     */
    @Operation(summary = "Delete DiplomaDefense",
            description = "Deletes the specified DiplomaDefense record.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "DiplomaDefense not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @Positive Long id,
            Authentication authentication) {
        diplomaDefenseService.deleteById(id, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the average number of students defended between two dates.
     */
    @Operation(summary = "Get average number of students defended between dates",
            description = "Returns the average number of students defended between the specified dates.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved average.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/average-students-defended/{startDate}-{endDate}")
    public ResponseEntity<Double> findAverageNumberOfStudentsDefendedBetweenDates(
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate) {
        Double average = diplomaDefenseService.findAverageNumberOfStudentsDefendedBetweenDates(startDate, endDate);
        return ResponseEntity.ok(average);
    }

    /**
     * Links a teacher to a diploma defense.
     */
    @Operation(summary = "Link a teacher to a diploma defense",
            description = "Links a teacher to a diploma defense.")
    @ApiResponse(responseCode = "200", description = "Successfully linked teacher.")
    @ApiResponse(responseCode = "404", description = "Diploma defense or teacher not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PutMapping("/{defenseId}/link-teacher/{teacherId}")
    public ResponseEntity<DiplomaDefenseDto> linkTeacher(
            @PathVariable @Positive Long defenseId,
            @PathVariable @Positive Long teacherId,
            Authentication authentication) {
        DiplomaDefenseDto defense = diplomaDefenseService.linkTeacher(defenseId, teacherId, authentication);
        return ResponseEntity.ok(defense);
    }


}
