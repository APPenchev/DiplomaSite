package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentService {
    List<StudentDto> findAll();

    StudentDto getById(Long id,Authentication auth);

    StudentDto getByFacultyNumber(String facultyNumber);
    StudentDto create(CreateStudentDto createStudentDto);
    StudentDto update(Long id, UpdateStudentDto updateStudentDto);
    void delete(Long id);
}
