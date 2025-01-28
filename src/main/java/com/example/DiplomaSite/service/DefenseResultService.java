package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDefenseResultDto;
import com.example.DiplomaSite.dto.DefenseResultDto;
import com.example.DiplomaSite.dto.UpdateDefenseResultDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DefenseResultService {

    List<DefenseResultDto> findAll();

    DefenseResultDto getById(
            @NotNull @Positive Long id,
            Authentication auth);

    DefenseResultDto create(
            @Valid CreateDefenseResultDto defenseResult);

    DefenseResultDto update(
            @NotNull @Positive Long id,
            UpdateDefenseResultDto defenseResult,
            Authentication auth
    );

    void deleteById(
            @NotNull @Positive Long id,
            Authentication auth
    );

    DefenseResultDto linkResultToDefense(
            @NotNull @Positive Long defenseResultId,
            @NotNull @Positive Long diplomaDefenseId,
            Authentication auth);


}
