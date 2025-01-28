package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.CreateDiplomaDefenseDto;
import com.example.DiplomaSite.dto.DiplomaDefenseDto;
import com.example.DiplomaSite.dto.UpdateDiplomaDefenseDto;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.*;
import com.example.DiplomaSite.repository.DefenseResultRepository;
import com.example.DiplomaSite.repository.DiplomaDefenseRepository;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import com.example.DiplomaSite.service.DefenseResultService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.example.DiplomaSite.service.DiplomaDefenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class DiplomaDefenseServiceImpl implements DiplomaDefenseService {

    private final DiplomaDefenseRepository diplomaDefenseRepository;
    private final TeacherRepository teacherRepository;
    private final DiplomaThesisRepository diplomaThesisRepository;
    private final DefenseResultRepository defenseResultRepository;
    private final DefenseResultService defenseResultService;

    @Autowired
    public DiplomaDefenseServiceImpl(
            DiplomaDefenseRepository diplomaDefenseRepository,
            TeacherRepository teacherRepository,
            DiplomaThesisRepository diplomaThesisRepository,
            DefenseResultRepository defenseResultRepository,
            DefenseResultService defenseResultService
            ) {
        this.diplomaDefenseRepository = diplomaDefenseRepository;
        this.teacherRepository = teacherRepository;
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.defenseResultRepository = defenseResultRepository;
        this.defenseResultService = defenseResultService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<DiplomaDefenseDto> findAll() {
        return diplomaDefenseRepository.findAll()
                .stream()
                .map(this::toDiplomaDefenseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin','teacher') or " +
            "(hasAnyAuthority('student') and @diplomaDefenseSecurity.isStudentThesis(#id, @keycloakUtils.getUserId(#auth)))")

    public List<DiplomaDefenseDto> findByThesisId(
            @NotNull @Positive Long id,
            Authentication auth) {
            return diplomaDefenseRepository.findAllByDiplomaThesisId(id)
                .stream()
                .map(this::toDiplomaDefenseDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin') or"+
            "(hasAnyAuthority('teacher') and @diplomaDefenseSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth))) or"+
            "(hasAnyAuthority('student') and @diplomaDefenseSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaDefenseDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    ) {
        return diplomaDefenseRepository.findById(id)
                .map(this::toDiplomaDefenseDto)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    public DiplomaDefenseDto create(
            @Valid CreateDiplomaDefenseDto diplomaDefense,
            Authentication auth) {
        if (diplomaDefense.getDate() == null) {
            throw new DiplomaDefenseValidationException("Date cannot be null");
        }
        DiplomaDefense newDiplomaDefense = new DiplomaDefense();
        newDiplomaDefense.setDate(diplomaDefense.getDate());

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("teacher"))) {
            String keycloakUserId = KeycloakUtils.getUserId(auth);
            Teacher teacher = teacherRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with keycloak user id: " + keycloakUserId));
            newDiplomaDefense.setSupervisor(teacher);
        }
        else {
            newDiplomaDefense.setSupervisor(teacherRepository.findById(diplomaDefense.getSupervisorId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + diplomaDefense.getSupervisorId())));
        }

        if (diplomaDefense.getCommitteeMembersIds() != null) {
            newDiplomaDefense.setTeachers(diplomaDefense.getCommitteeMembersIds().stream()
                    .map(id -> teacherRepository.findById(id)
                            .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id)))
                    .collect(Collectors.toList()));
        }

        DiplomaThesis thesis = diplomaThesisRepository.findById(diplomaDefense.getThesisId())
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + diplomaDefense.getThesisId()));

        newDiplomaDefense.setDiplomaThesis(thesis);

        try {
            DiplomaDefense savedDiplomaDefense = diplomaDefenseRepository.save(newDiplomaDefense);
            return toDiplomaDefenseDto(savedDiplomaDefense);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException("Unable to create diploma defense due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaDefenseSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaDefenseDto update(
            @NotNull @Positive Long id,
            UpdateDiplomaDefenseDto diplomaDefense,
            Authentication auth) {
        DiplomaDefense existingDiplomaDefense = diplomaDefenseRepository.findById(id)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + id));


        if (diplomaDefense.getDate() != null) {
            existingDiplomaDefense.setDate(diplomaDefense.getDate());
        }

        if (diplomaDefense.getSupervisorId() != null) {
            Teacher newSupervisor = teacherRepository.findById(diplomaDefense.getSupervisorId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + diplomaDefense.getSupervisorId()));
            existingDiplomaDefense.getTeachers().add(newSupervisor);
            if (existingDiplomaDefense.getTeachers().contains(newSupervisor)) {
                existingDiplomaDefense.getTeachers().remove(newSupervisor);
            }
        }


        try {
            existingDiplomaDefense = diplomaDefenseRepository.save(existingDiplomaDefense);
            return toDiplomaDefenseDto(existingDiplomaDefense);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException("Unable to update diploma defense due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaDefenseSecurity.isSupervisor(#defenseResultId, @keycloakUtils.getUserId(#auth)))")
    public DiplomaDefenseDto linkDefensetoThesis(
            @NotNull @Positive Long defenseResultId,
            @NotNull @Positive Long thesisId,
            Authentication auth) {
        DiplomaDefense defense = diplomaDefenseRepository.findById(defenseResultId)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + defenseResultId));
        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + thesisId));


        if (defense.getDiplomaThesis() != null) {
            defense.getDiplomaThesis().getDiplomaDefenses().remove(defense);
        }
        defense.setDiplomaThesis(thesis);

        if (thesis.getDiplomaDefenses().contains(defense)) {
            throw new DefenseResultCreationException("Defense result is already linked to this thesis", null);
        }
        thesis.getDiplomaDefenses().add(defense);

        try {
            defense = diplomaDefenseRepository.save(defense);
            diplomaThesisRepository.save(thesis);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to link defense result to thesis due to data integrity issues", e);
        }

        return toDiplomaDefenseDto(defense);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaDefenseSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(@NotNull @Positive Long id, Authentication auth) {
        DiplomaDefense defense = diplomaDefenseRepository.findById(id)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + id));

        if (defense.getDefenseResult() != null) {
            Long defenseResultId = defense.getDefenseResult().getId();

            // Delete the child first
            defenseResultService.deleteById(defenseResultId, auth);

            // Break the reference in the parent
            defense.setDefenseResult(null);
            // No immediate flush required if the entire method is @Transactional
        }

        DiplomaThesis thesis = defense.getDiplomaThesis();  // keep a local reference
        if (thesis != null) {
            // remove defense from parent's list
            thesis.getDiplomaDefenses().remove(defense);

            // remove parent from child's reference
            defense.setDiplomaThesis(null);

            // now save using the local variable (which is not null)
            diplomaThesisRepository.save(thesis);
            diplomaThesisRepository.flush();
        }

        // Now safely remove the defense
        try {
            diplomaDefenseRepository.deleteById(id);
            // Or just diplomaDefenseRepository.delete(defense);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException(
                    "Unable to delete diploma defense due to data integrity issues", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin')")
    public Double findAverageNumberOfStudentsDefendedBetweenDates(@NotNull LocalDate startDate,@NotNull LocalDate endDate) {
        return diplomaDefenseRepository.findAverageNumberOfStudentsDefendedBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaResultSecurity.isDefenceTeacherThesis(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasAnyAuthority('student') and @diplomaResultSecurity.isStudentThesis(#id, @keycloakUtils.getUserId(#auth)))")
    public List<DiplomaDefenseDto> findAllByThesisId(
            @NotNull @Positive Long thesisId,
            Authentication auth) {
        return diplomaDefenseRepository.findAllByDiplomaThesisId(thesisId)
                .stream()
                .map(this::toDiplomaDefenseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaDefenseSecurity.isSupervisor(#defenseId, @keycloakUtils.getUserId(#auth)))")
    public DiplomaDefenseDto linkTeacher(
            @NotNull @Positive Long defenseId,
            @NotNull @Positive Long teacherId,
            Authentication auth) {
        DiplomaDefense defense = diplomaDefenseRepository.findById(defenseId)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + defenseId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + teacherId));


        if (defense.getTeachers().contains(teacher)) {
            throw new DiplomaDefenseCreationException("Teacher is already linked to this defense", null);
        }
        defense.getTeachers().add(teacher);

        if (teacher.getDefenses().contains(defense)) {
            throw new DiplomaDefenseCreationException("Defense is already linked to this teacher", null);
        }
        teacher.getDefenses().add(defense);

        try {
            defense = diplomaDefenseRepository.save(defense);
            teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException("Unable to link teacher to defense due to data integrity issues", e);
        }
        return toDiplomaDefenseDto(defense);
    }

    public DiplomaDefenseDto toDiplomaDefenseDto(DiplomaDefense diplomaDefense) {
        DiplomaDefenseDto diplomaDefenseDto = new DiplomaDefenseDto();
        diplomaDefenseDto.setId(diplomaDefense.getId());
        diplomaDefenseDto.setDate(diplomaDefense.getDate());
        if (diplomaDefense.getSupervisor() != null) {
            diplomaDefenseDto.setSupervisorKeycloakId(diplomaDefense.getSupervisor().getKeycloakUserId());
        }
        if (!diplomaDefense.getTeachers().isEmpty()) {
            diplomaDefenseDto.setCommitteeMembersKeycloakIds(diplomaDefense.getTeachers().stream()
                    .map(Teacher::getKeycloakUserId)
                    .collect(Collectors.toList()));
        }
        if (diplomaDefense.getDefenseResult() != null) {
            diplomaDefenseDto.setResultId(diplomaDefense.getDefenseResult().getId());
        }
        return diplomaDefenseDto;
    }




}
