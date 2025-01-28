package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaAssignmentDto;
import com.example.DiplomaSite.dto.DiplomaAssignmentDto;
import com.example.DiplomaSite.dto.UpdateDiplomaAssignmentDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import com.example.DiplomaSite.dto.AssignmentStatusProjection;

import java.util.List;

public interface DiplomaAssignmentService {


    List<DiplomaAssignmentDto> findAll();

    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgress();

    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgressByTopic(@NotNull String topic);

    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgressByTeacher(@NotNull String teacher);

    DiplomaAssignmentDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    );

    DiplomaAssignmentDto create(@Valid CreateDiplomaAssignmentDto createDiplomaAssignmentDto, Authentication auth);

    DiplomaAssignmentDto update(
            @Positive @NotNull Long id,
            UpdateDiplomaAssignmentDto updateDiplomaAssignmentDto,
            Authentication auth);

    void deleteById(@NotNull @Positive Long id, Authentication auth);

    List<DiplomaAssignmentDto> findByApprovedTrue();

    List<DiplomaAssignmentDto> findBySupervisorAndApprovedTrue(@NotNull @Positive Long supervisorId);

    DiplomaAssignmentDto linkAssignmentToStudent(
            @NotNull @Positive Long assignmentId,
            @NotNull @Positive Long studentId,
            Authentication auth);

    DiplomaAssignmentDto linkSupervisor(
            @NotNull @Positive Long assignmentId,
            @NotNull @Positive Long teacherId,
            Authentication auth);

    DiplomaAssignmentDto approve(@NotNull Long id, Authentication auth);

    DiplomaAssignmentDto getByToken(Authentication auth);

}
