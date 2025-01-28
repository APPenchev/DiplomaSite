package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaDefenseDto;
import com.example.DiplomaSite.dto.DefenseResultDto;
import com.example.DiplomaSite.dto.DiplomaDefenseDto;
import com.example.DiplomaSite.dto.UpdateDiplomaDefenseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface DiplomaDefenseService {


    List<DiplomaDefenseDto> findAll();

    List<DiplomaDefenseDto> findByThesisId(
            @NotNull @Positive Long id,
            Authentication auth);

    DiplomaDefenseDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    );

    DiplomaDefenseDto create(
            @Valid CreateDiplomaDefenseDto diplomaDefense,
            Authentication auth);

    DiplomaDefenseDto update(
            @NotNull @Positive Long id,
            UpdateDiplomaDefenseDto diplomaDefense,
            Authentication auth);

    DiplomaDefenseDto linkDefensetoThesis(
            @NotNull @Positive Long defenseResultId,
            @NotNull @Positive Long thesisId,
            Authentication auth);

    void deleteById(
            @NotNull @Positive Long id,
            Authentication auth);

    Double findAverageNumberOfStudentsDefendedBetweenDates(@NotNull LocalDate startDate, @NotNull LocalDate endDate);

    List<DiplomaDefenseDto> findAllByThesisId(
            @NotNull @Positive Long thesisId,
            Authentication auth);


    DiplomaDefenseDto linkTeacher(
            @NotNull @Positive Long defenseId,
            @NotNull @Positive Long teacherId,
            Authentication auth);
}
