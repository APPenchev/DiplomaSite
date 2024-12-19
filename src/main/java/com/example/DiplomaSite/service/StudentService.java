package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import com.example.DiplomaSite.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<StudentDto> findAll();
    StudentDto getById(Long id);
    StudentDto getByFacultyNumber(String facultyNumber);
    StudentDto create(CreateStudentDto createStudentDto);
    StudentDto update(Long id, UpdateStudentDto updateStudentDto);
    void deleteById(Long id);
}
