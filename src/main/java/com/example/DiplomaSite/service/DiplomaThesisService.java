package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaThesisDto;
import com.example.DiplomaSite.dto.DiplomaThesisDto;
import com.example.DiplomaSite.dto.UpdateDiplomaThesisDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DiplomaThesisService {



    List<DiplomaThesisDto> findAll(Authentication authentication);

    DiplomaThesisDto getById(@NotNull @Positive Long id,
                             Authentication auth);

    DiplomaThesisDto create(
            @Valid CreateDiplomaThesisDto diplomaThesis,
            Authentication auth);

    DiplomaThesisDto update(
            @NotNull @Positive Long id,
            UpdateDiplomaThesisDto diplomaThesisDto,
            Authentication auth);

    void deleteById(
            @NotNull @Positive Long id,
            Authentication authentication);

    List<DiplomaThesisDto> findByGradeBetween(
            @NotNull @Positive Double minGrade,
            @NotNull @Positive Double maxGrade);
    
    DiplomaThesisDto linkThesisToAssignment(
            @NotNull @Positive Long thesisId,
            @NotNull @Positive Long assignmentId,
            Authentication authentication);

    DiplomaThesisDto getByToken(Authentication authentication);
}
