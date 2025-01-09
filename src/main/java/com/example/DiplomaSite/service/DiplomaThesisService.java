package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaThesisDto;
import com.example.DiplomaSite.dto.DiplomaThesisDto;
import com.example.DiplomaSite.dto.UpdateDiplomaThesisDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DiplomaThesisService {

    List<DiplomaThesisDto> findAll(Authentication authentication);

    DiplomaThesisDto getById(Long id, Authentication authentication);
    DiplomaThesisDto create(CreateDiplomaThesisDto diplomaThesis);
    DiplomaThesisDto update(Long id, UpdateDiplomaThesisDto diplomaThesisDto, Authentication authentication);
    void deleteById(Long id, Authentication authentication);
    List<DiplomaThesisDto> findByGradeBetween(Double minGrade, Double maxGrade);

    DiplomaThesisDto linkThesisToAssignment(Long thesisId, Long assignmentId, Authentication authentication);
}
