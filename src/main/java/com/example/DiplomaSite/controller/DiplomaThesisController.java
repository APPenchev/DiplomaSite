package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateDiplomaThesisDto;
import com.example.DiplomaSite.dto.DiplomaThesisDto;
import com.example.DiplomaSite.dto.UpdateDiplomaThesisDto;
import com.example.DiplomaSite.service.DiplomaThesisService;
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

@RestController
@RequestMapping("/api/diploma-thesis")
@Validated
public class DiplomaThesisController {

    private final DiplomaThesisService diplomaThesisService;

    @Autowired
    public DiplomaThesisController(DiplomaThesisService diplomaThesisService) {
        this.diplomaThesisService = diplomaThesisService;
    }

    /**
     * Retrieves all existing DiplomaTheses.
     */

    @Operation(summary = "Get all DiplomaTheses",
            description = "Returns a list of all DiplomaThesis records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<DiplomaThesisDto>> findAll(Authentication authentication) {
        List<DiplomaThesisDto> theses = diplomaThesisService.findAll(authentication);
        return ResponseEntity.ok(theses);
    }

    /**
     * Retrieves a single DiplomaThesis by its ID.
     */
    @Operation(summary = "Get DiplomaThesis by ID",
            description = "Returns the DiplomaThesis with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "DiplomaThesis not found.")
    @RolesAllowed({"ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<DiplomaThesisDto> getById(
            @PathVariable @Positive Long id,
            Authentication authentication) {
        DiplomaThesisDto thesis = diplomaThesisService.getById(id, authentication);
        return ResponseEntity.ok(thesis);
    }

    /**
     * Creates a new DiplomaThesis.
     */
    @Operation(summary = "Create DiplomaThesis",
            description = "Creates a new DiplomaThesis record.")
    @ApiResponse(responseCode = "201", description = "Successfully created record.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @RolesAllowed({"ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<DiplomaThesisDto> create(
            @RequestBody @Valid CreateDiplomaThesisDto diplomaThesisDto) {
        DiplomaThesisDto thesis = diplomaThesisService.create(diplomaThesisDto);
        return new ResponseEntity<>(thesis, HttpStatus.CREATED);
    }

    /**
     * Updates an existing DiplomaThesis.
     */
    @Operation(summary = "Update DiplomaThesis",
            description = "Updates an existing DiplomaThesis record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "DiplomaThesis not found.")
    @RolesAllowed({"ROLE_TEACHER", "ROLE_ADMIN", "ROLE_STUDENT"})
    @PutMapping("/{id}")
    public ResponseEntity<DiplomaThesisDto> update(
            @PathVariable @Positive Long id,
            @RequestBody UpdateDiplomaThesisDto diplomaThesisDto,
            Authentication authentication) {
        DiplomaThesisDto thesis = diplomaThesisService.update(id, diplomaThesisDto, authentication);
        return ResponseEntity.ok(thesis);
    }

    /**
     * Deletes a DiplomaThesis by its ID.
     */
    @Operation(summary = "Delete DiplomaThesis",
            description = "Deletes the DiplomaThesis with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "DiplomaThesis not found.")
    @RolesAllowed({"ROLE_TEACHER", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public void deleteById(
            @PathVariable @Positive Long id,
            Authentication authentication) {
        diplomaThesisService.deleteById(id, authentication);
    }

    /**
     * Links DiplomaThesis to DiplomaAssignment.
     */
    @Operation(summary = "Link DiplomaThesis to DiplomaAssignment",
            description = "Links the specified DiplomaThesis to the specified DiplomaAssignment.")
    @ApiResponse(responseCode = "200", description = "Successfully linked records.")
    @ApiResponse(responseCode = "404", description = "DiplomaThesis or DiplomaAssignment not found.")
    @RolesAllowed({"ROLE_TEACHER", "ROLE_ADMIN"})
    @PutMapping("/{thesisId}/link-assignment/{assignmentId}")
    public ResponseEntity<DiplomaThesisDto> linkThesisToAssignment(
            @PathVariable @Positive Long thesisId,
            @PathVariable @Positive Long assignmentId,
            Authentication authentication) {
        DiplomaThesisDto thesis = diplomaThesisService.linkThesisToAssignment(thesisId, assignmentId, authentication);
        return ResponseEntity.ok(thesis);
    }

    /**
     * Retrieves all DiplomaTheses with grades between minGrade and maxGrade.
     */
    @Operation(summary = "Get DiplomaTheses by Grade Range",
            description = "Returns a list of all DiplomaTheses with grades between minGrade and maxGrade.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @RolesAllowed({"ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN"})
    @GetMapping("/grade-range/{minGrade}/{maxGrade}")
    public ResponseEntity<List<DiplomaThesisDto>> findByGradeBetween(
            @PathVariable @Positive Double minGrade,
            @PathVariable @Positive Double maxGrade) {
        List<DiplomaThesisDto> theses = diplomaThesisService.findByGradeBetween(minGrade, maxGrade);
        return ResponseEntity.ok(theses);
    }



}
