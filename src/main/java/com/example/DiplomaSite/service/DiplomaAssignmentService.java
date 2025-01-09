package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateDiplomaAssignmentDto;
import com.example.DiplomaSite.dto.DiplomaAssignmentDto;
import com.example.DiplomaSite.dto.UpdateDiplomaAssignmentDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DiplomaAssignmentService {

    List<DiplomaAssignmentDto> findAll();
    DiplomaAssignmentDto getById(Long id, Authentication authentication);
    DiplomaAssignmentDto create(CreateDiplomaAssignmentDto createDiplomaAssignmentDto);
    DiplomaAssignmentDto update(Long id, UpdateDiplomaAssignmentDto updateDiplomaAssignmentDto, Authentication authentication);
    void deleteById(Long id);

    List<DiplomaAssignmentDto> findByApprovedTrue();
    List<DiplomaAssignmentDto> findByTopicContainingIgnoreCase(String partialTopic);
    List<DiplomaAssignmentDto> findBySupervisorAndApprovedTrue(Long supervisorId);

    DiplomaAssignmentDto linkAssignmentToStudent(Long assignmentId, Long studentId, Authentication authentication);
    DiplomaAssignmentDto linkSupervisor(Long assignmentId, Long teacherId, Authentication authentication);
}
