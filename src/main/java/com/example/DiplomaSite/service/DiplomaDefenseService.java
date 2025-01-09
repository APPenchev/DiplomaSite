package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaDefenseDto;
import com.example.DiplomaSite.dto.DiplomaDefenseDto;
import com.example.DiplomaSite.dto.UpdateDiplomaDefenseDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface DiplomaDefenseService {

    List<DiplomaDefenseDto> findAll();
    DiplomaDefenseDto getById(Long id, Authentication auth);
    DiplomaDefenseDto create(CreateDiplomaDefenseDto diplomaDefense, Authentication authentication);
    DiplomaDefenseDto update(Long id, UpdateDiplomaDefenseDto diplomaDefense, Authentication authentication);
    void deleteById(Long id, Authentication authentication);

    Double findAverageNumberOfStudentsDefendedBetweenDates(LocalDate startDate, LocalDate endDate);
    DiplomaDefenseDto linkTeacher(Long defenseId, Long teacherId, Authentication authentication);
}
