package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface StudentService {

    List<StudentDto> findAll();

    StudentDto getById(
            @NotNull @Positive Long id,
            Authentication auth);

    List<StudentDto> getStudentByName(@NotNull String name);

    StudentDto getByFacultyNumber(@NotNull String facultyNumber);

    StudentDto create(@Valid CreateStudentDto createStudentDto);

    StudentDto update(@NotNull @Positive Long id, UpdateStudentDto updateStudentDto);

    void delete(
            @NotNull @Positive Long id,
            Authentication auth);

    List<StudentDto> getStudentsWhoPassedBetweenDates(@NotNull LocalDate startDate, @NotNull LocalDate endDate);
}
