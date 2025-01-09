package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateDiplomaAssignmentDto;
import com.example.DiplomaSite.dto.DiplomaAssignmentDto;
import com.example.DiplomaSite.dto.UpdateDiplomaAssignmentDto;
import com.example.DiplomaSite.service.DiplomaAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * REST controller for Managing Diploma Assignments.
 */
@RestController
@RequestMapping("/api/diploma-assignments")
@Validated
public class DiplomaAssignmentController {

    private final DiplomaAssignmentService diplomaAssignmentService;

    @Autowired
    public DiplomaAssignmentController(DiplomaAssignmentService diplomaAssignmentService) {
        this.diplomaAssignmentService = diplomaAssignmentService;
    }

    /**
     * Retrieves all existing DiplomaAssignments.
     */
    @Operation(summary = "Get all DiplomaAssignments",
            description = "Returns a list of all DiplomaAssignment records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping
    public ResponseEntity<List<DiplomaAssignmentDto>> findAll() {
        List<DiplomaAssignmentDto> assignments = diplomaAssignmentService.findAll();
        return ResponseEntity.ok(assignments);
    }

    /**
     * Retrieves a single DiplomaAssignment by its ID.
     */
    @Operation(summary = "Get DiplomaAssignment by ID",
            description = "Returns the DiplomaAssignment with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "DiplomaAssignment not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/{id}")
    public ResponseEntity<DiplomaAssignmentDto> getById(
            @PathVariable @Positive Long id,
            Authentication auth) {
        DiplomaAssignmentDto assignment = diplomaAssignmentService.getById(id, auth);
        return ResponseEntity.ok(assignment);
    }

    /**
     * Creates a new DiplomaAssignment.
     */
    @Operation(summary = "Create new DiplomaAssignment",
            description = "Creates a new DiplomaAssignment record.")
    @ApiResponse(responseCode = "201", description = "Successfully created record.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PostMapping
    public ResponseEntity<DiplomaAssignmentDto> create(
            @Valid @RequestBody CreateDiplomaAssignmentDto assignment) {
        DiplomaAssignmentDto newAssignment = diplomaAssignmentService.create(assignment);
        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    /**
     * Updates an existing DiplomaAssignment.
     */
    @Operation(summary = "Update DiplomaAssignment",
            description = "Updates and returns the updated DiplomaAssignment record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "DiplomaAssignment not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PutMapping("/{id}")
    public ResponseEntity<DiplomaAssignmentDto> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateDiplomaAssignmentDto assignment,
            Authentication auth) {
        DiplomaAssignmentDto updatedAssignment = diplomaAssignmentService.update(id, assignment, auth);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Deletes an existing DiplomaAssignment.
     */
    @Operation(summary = "Delete DiplomaAssignment",
            description = "Deletes the specified DiplomaAssignment record.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "DiplomaAssignment not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive Long id) {
        diplomaAssignmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Links a DiplomaAssignment to a Student.
     */
    @Operation(summary = "Link a DiplomaAssignment to a Student",
            description = "Links the specified DiplomaAssignment to the specified Student.")
    @ApiResponse(responseCode = "200", description = "Successfully linked records.")
    @ApiResponse(responseCode = "404", description = "DiplomaAssignment or Student not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PutMapping("/{assignmentId}/link-student/{studentId}")
    public ResponseEntity<DiplomaAssignmentDto> linkAssignmentToStudent(
            @PathVariable @Positive Long assignmentId,
            @PathVariable @Positive Long studentId,
            Authentication auth) {
        DiplomaAssignmentDto linkedAssignment = diplomaAssignmentService.linkAssignmentToStudent(assignmentId, studentId, auth);
        return ResponseEntity.ok(linkedAssignment);
    }

    /**
     * Links a DiplomaAssignment to a Supervisor.
     */
    @Operation(summary = "Link a DiplomaAssignment to a Supervisor",
            description = "Links the specified DiplomaAssignment to the specified Supervisor.")
    @ApiResponse(responseCode = "200", description = "Successfully linked records.")
    @ApiResponse(responseCode = "404", description = "DiplomaAssignment or Supervisor not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @PutMapping("/{assignmentId}/link-supervisor/{teacherId}")
    public ResponseEntity<DiplomaAssignmentDto> linkSupervisor(
            @PathVariable @Positive Long assignmentId,
            @PathVariable @Positive Long teacherId,
            Authentication auth) {
        DiplomaAssignmentDto linkedAssignment = diplomaAssignmentService.linkSupervisor(assignmentId, teacherId, auth);
        return ResponseEntity.ok(linkedAssignment);
    }

    /**
     * Retrieves all existing DiplomaAssignments that are approved.
     */
    @Operation(summary = "Get all approved DiplomaAssignments",
            description = "Returns a list of all approved DiplomaAssignment records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/approved")
    public ResponseEntity<List<DiplomaAssignmentDto>> findByApprovedTrue() {
        List<DiplomaAssignmentDto> assignments = diplomaAssignmentService.findByApprovedTrue();
        return ResponseEntity.ok(assignments);
    }

    /**
     * Retrieves all existing DiplomaAssignments that contain a specified topic.
     */
    @Operation(summary = "Get all DiplomaAssignments by topic",
            description = "Returns a list of all DiplomaAssignment records that contain the specified topic.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<DiplomaAssignmentDto>> findByTopicContainingIgnoreCase(
            @PathVariable String topic) {
        List<DiplomaAssignmentDto> assignments = diplomaAssignmentService.findByTopicContainingIgnoreCase(topic);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Retrieves all existing DiplomaAssignments that are approved and supervised by a specified teacher.
     */
    @Operation(summary = "Get all approved DiplomaAssignments by supervisor",
            description = "Returns a list of all approved DiplomaAssignment records supervised by the specified teacher.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/supervisor/{teacherId}")
    public ResponseEntity<List<DiplomaAssignmentDto>> findBySupervisorAndApprovedTrue(
            @PathVariable @Positive Long teacherId) {
        List<DiplomaAssignmentDto> assignments = diplomaAssignmentService.findBySupervisorAndApprovedTrue(teacherId);
        return ResponseEntity.ok(assignments);
    }



}
