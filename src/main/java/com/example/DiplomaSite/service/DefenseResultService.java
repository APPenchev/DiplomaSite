package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDefenseResultDto;
import com.example.DiplomaSite.dto.DefenseResultDto;
import com.example.DiplomaSite.dto.UpdateDefenseResultDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DefenseResultService {
    List<DefenseResultDto> findAll();
    List<DefenseResultDto> findAllByThesisId(Long thesisId, Authentication authentication);
    DefenseResultDto getById(Long id, Authentication authentication);
    DefenseResultDto create(CreateDefenseResultDto defenseResult);
    DefenseResultDto update(Long id, UpdateDefenseResultDto defenseResult, Authentication authentication);
    void deleteById(Long id, Authentication authentication);

    DefenseResultDto linkResultToThesis(Long defenseResultId, Long thesisId, Authentication authentication);
    DefenseResultDto linkResultToDefense(Long defenseResultId, Long diplomaDefenseId, Authentication authentication);
}
