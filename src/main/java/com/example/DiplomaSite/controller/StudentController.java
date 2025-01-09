package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import com.example.DiplomaSite.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieves all existing Students.
     */
    @Operation(summary = "Get all Students",
            description = "Returns a list of all Student records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping
    public ResponseEntity<List<StudentDto>> findAll() {
        List<StudentDto> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    /**
     * Retrieves a single Student by its ID.
     */
    @Operation(summary = "Get Student by ID",
            description = "Returns the Student with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "Student not found.")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getById(
            @PathVariable @Positive Long id,
            Authentication auth) {
        StudentDto student = studentService.getById(id, auth);
        return ResponseEntity.ok(student);
    }

    /**
     * Creates a new Student.
     */
    @Operation(summary = "Create Student",
            description = "Creates a new Student record.")
    @ApiResponse(responseCode = "201", description = "Successfully created record.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<StudentDto> create(
            @RequestBody @Valid CreateStudentDto studentDto) {
        StudentDto student = studentService.create(studentDto);
        return ResponseEntity.ok(student);
    }

    /**
     * Updates an existing Student.
     */
    @Operation(summary = "Update Student",
            description = "Updates an existing Student record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "Student not found.")
    @RolesAllowed({"ROLE_ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateStudentDto studentDto) {
        StudentDto student = studentService.update(id, studentDto);
        return ResponseEntity.ok(student);
    }

    /**
     * Deletes an existing Student.
     */
    @Operation(summary = "Delete Student",
            description = "Deletes an existing Student record.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "Student not found.")
    @RolesAllowed({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
    


}
