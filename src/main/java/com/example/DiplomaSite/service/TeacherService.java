package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateTeacherDto;
import com.example.DiplomaSite.dto.UpdateTeacherDto;
import com.example.DiplomaSite.dto.TeacherDto;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeacherService {


    List<TeacherDto> findAll();

    TeacherDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    );


    TeacherDto findTeacherIdByKeycloakId(@NotNull String keycloakId,
                                         Authentication auth);

    TeacherDto create(@Valid CreateTeacherDto createTeacherDto);

    TeacherDto update(@Positive @NotNull Long id, UpdateTeacherDto teacher);

    void delete(
            @Positive @NotNull Long id,
            Authentication auth);

    List<TeacherDto> getTeachersByName(@NotNull String namePart);

    List<TeacherDto> getTeachersByPosition(@NotNull TeacherPosition position);

    Long countSuccessfullyGraduatedStudentsForTeacher(
            @NotNull @Positive Long teacherId,
            @NotNull @Positive Double passingGrade,
            Authentication auth);

}
