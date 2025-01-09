package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateTeacherDto;
import com.example.DiplomaSite.dto.UpdateTeacherDto;
import com.example.DiplomaSite.dto.TeacherDto;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeacherService {
    List<TeacherDto> findAll();

    TeacherDto getById(Long id, Authentication auth);

    TeacherDto create(CreateTeacherDto teacher);
    TeacherDto update(Long id, UpdateTeacherDto teacher);
    void delete(Long id);

    List<TeacherDto> getTeachersByName(String namePart);
    List<TeacherDto> getTeachersByPosition(TeacherPosition position);
    Long countSuccessfullyGraduatedStudentsForTeacher(Long teacherId, Double passingGrade, Authentication auth);

}
