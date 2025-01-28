package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateDefenseResultDto;
import com.example.DiplomaSite.dto.DefenseResultDto;
import com.example.DiplomaSite.dto.UpdateDefenseResultDto;
import com.example.DiplomaSite.service.DefenseResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Defense Results.
 */
@RestController
@RequestMapping("/api/defense-results")
@Validated
public class DefenseResultController {

    private final DefenseResultService defenseResultService;

    @Autowired
    public DefenseResultController(DefenseResultService defenseResultService) {
        this.defenseResultService = defenseResultService;
    }

    /**
     * Retrieves all existing DefenseResults.
     */
    @Operation(summary = "Get all DefenseResults",
            description = "Returns a list of all DefenseResult records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping
    public ResponseEntity<List<DefenseResultDto>> findAll() {
        List<DefenseResultDto> defenseResults = defenseResultService.findAll();
        return ResponseEntity.ok(defenseResults);
    }

    /**
     * Retrieves a single DefenseResult by its ID.
     */
    @Operation(summary = "Get DefenseResult by ID",
            description = "Returns the DefenseResult with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "DefenseResult not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher','student')")
    @GetMapping("/{id}")
    public ResponseEntity<DefenseResultDto> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            Authentication authentication) {
        DefenseResultDto result = defenseResultService.getById(id, authentication);
        return ResponseEntity.ok(result);
    }



    /**
     * Creates a new DefenseResult.
     */
    @Operation(summary = "Create new DefenseResult",
            description = "Creates and returns a new DefenseResult record.")
    @ApiResponse(responseCode = "201", description = "Successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @PostMapping
    public ResponseEntity<DefenseResultDto> create(
            @Valid @RequestBody CreateDefenseResultDto createDefenseResultDto) {
        DefenseResultDto createdResult = defenseResultService.create(createDefenseResultDto);
        return new ResponseEntity<>(createdResult, HttpStatus.CREATED);
    }

    /**
     * Updates an existing DefenseResult by its ID.
     */
    @Operation(summary = "Update DefenseResult",
            description = "Updates the specified DefenseResult and returns the updated record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated.")
    @ApiResponse(responseCode = "404", description = "DefenseResult not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @PutMapping("/{id}")
    public ResponseEntity<DefenseResultDto> update(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody UpdateDefenseResultDto updateDefenseResultDto,
            Authentication authentication) {
        DefenseResultDto updatedResult = defenseResultService.update(id, updateDefenseResultDto, authentication);
        return ResponseEntity.ok(updatedResult);
    }

    /**
     * Deletes an existing DefenseResult by its ID.
     */
    @Operation(summary = "Delete DefenseResult",
            description = "Deletes the DefenseResult with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted, no content.")
    @ApiResponse(responseCode = "404", description = "DefenseResult not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            Authentication authentication) {
        defenseResultService.deleteById(id, authentication);
        return ResponseEntity.noContent().build();
    }


    /**
     * Links an existing DefenseResult to a DiplomaDefense by their IDs.
     */
    @Operation(summary = "Link DefenseResult to a DiplomaDefense",
            description = "Associates the specified DefenseResult with the specified DiplomaDefense.")
    @ApiResponse(responseCode = "200", description = "Successfully linked.")
    @ApiResponse(responseCode = "404", description = "Either DefenseResult or DiplomaDefense not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @PutMapping("/{defenseResultId}/link-defense/{diplomaDefenseId}")
    public ResponseEntity<DefenseResultDto> linkResultToDefense(
            @PathVariable @Positive(message = "ID must be a positive number") Long defenseResultId,
            @PathVariable @Positive(message = "ID must be a positive number") Long diplomaDefenseId,
            Authentication authentication) {
        DefenseResultDto linkedResult = defenseResultService.linkResultToDefense(defenseResultId, diplomaDefenseId, authentication);
        return ResponseEntity.ok(linkedResult);
    }
}