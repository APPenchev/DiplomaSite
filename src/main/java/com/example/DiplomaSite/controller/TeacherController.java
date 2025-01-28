package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateTeacherDto;
import com.example.DiplomaSite.dto.TeacherDto;
import com.example.DiplomaSite.dto.UpdateTeacherDto;
import com.example.DiplomaSite.enums.TeacherPosition;
import com.example.DiplomaSite.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/teachers")
@Validated
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Retrieves all existing Teachers.
     */
    @Operation(summary = "Get all Teachers",
            description = "Returns a list of all Teacher records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @GetMapping
    public ResponseEntity<List<TeacherDto>> findAll() {
        List<TeacherDto> teachers = teacherService.findAll();
        return ResponseEntity.ok(teachers);
    }

    /**
     * Retrieves a single Teacher by its ID.
     */
    @Operation(summary = "Get Teacher by ID",
            description = "Returns the Teacher with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> getById(
            @PathVariable @Positive Long id,
            Authentication auth) {
        TeacherDto teacher = teacherService.getById(id, auth);
        return ResponseEntity.ok(teacher);
    }

    /**
     * Creates a new Teacher.
     */
    @Operation(summary = "Create Teacher",
            description = "Creates a new Teacher record.")
    @ApiResponse(responseCode = "201", description = "Teacher created.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping
    public ResponseEntity<TeacherDto> create(
            @RequestBody @Valid CreateTeacherDto createTeacherDto) {
        TeacherDto teacher = teacherService.create(createTeacherDto);
        return new ResponseEntity<>(teacher, HttpStatus.CREATED);
    }

    /**
     * Updates an existing Teacher.
     */
    @Operation(summary = "Update Teacher",
            description = "Updates an existing Teacher record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateTeacherDto updateTeacherDto) {
        TeacherDto teacher = teacherService.update(id, updateTeacherDto);
        return ResponseEntity.ok(teacher);
    }

    /**
     * Deletes an existing Teacher.
     */
    @Operation(summary = "Delete Teacher",
            description = "Deletes an existing Teacher record.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive Long id,
            Authentication auth) {
        teacherService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all Teachers with a name containing the specified string.
     */
    @Operation(summary = "Get Teachers by name",
            description = "Returns a list of Teachers with names containing the specified string.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/name/{namePart}")
    public ResponseEntity<List<TeacherDto>> getTeachersName(
            @PathVariable @NotBlank String namePart) {
        List<TeacherDto> teachers = teacherService.getTeachersByName(namePart);
        return ResponseEntity.ok(teachers);
    }

    /**
     * Retrieves the ID of the Teacher with the specified Keycloak ID.
     */
    @Operation(summary = "Get Teacher ID by Keycloak ID",
            description = "Returns the ID of the Teacher with the specified Keycloak ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ID.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<TeacherDto> getTeacherIdByKeycloakId(
            @PathVariable String keycloakId,
            Authentication auth
    ) {
        TeacherDto teacherId = teacherService.findTeacherIdByKeycloakId(keycloakId, auth);
        if (teacherId != null) {
            return ResponseEntity.ok(teacherId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all Teachers with the specified position.
     */
    @Operation(summary = "Get Teachers by position",
            description = "Returns a list of Teachers with the specified position.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/position/{position}")
    public ResponseEntity<List<TeacherDto>> getTeachersPosition(
            @PathVariable TeacherPosition position) {
        List<TeacherDto> teachers = teacherService.getTeachersByPosition(position);
        return ResponseEntity.ok(teachers);
    }

    /**
     * Retrieves the number of students who successfully graduated under the specified teacher.
     */
    @Operation(summary = "Get number of students who successfully graduated under teacher",
            description = "Returns the number of students who successfully graduated under the specified teacher.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count.")
    @ApiResponse(responseCode = "404", description = "Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    @GetMapping("/{teacherId}/count-successful-graduates/{passingGrade}")
    public ResponseEntity<Long> countSuccessfullyGraduatedStudentsForTeacher(
            @PathVariable @Positive Long teacherId,
            @PathVariable @Positive Double passingGrade,
            Authentication auth) {
        Long count = teacherService.countSuccessfullyGraduatedStudentsForTeacher(teacherId, passingGrade, auth);
        return ResponseEntity.ok(count);
    }


}
