package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.CreateDiplomaDefenseDto;
import com.example.DiplomaSite.dto.DiplomaDefenseDto;
import com.example.DiplomaSite.dto.UpdateDiplomaDefenseDto;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.DiplomaDefenseCreationException;
import com.example.DiplomaSite.error.DiplomaDefenseNotFoundException;
import com.example.DiplomaSite.error.DiplomaDefenseValidationException;
import com.example.DiplomaSite.error.TeacherNotFoundException;
import com.example.DiplomaSite.repository.DiplomaDefenseRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import jakarta.annotation.security.RolesAllowed;
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

@Service
@Validated
public class DiplomaDefenseServiceImpl implements DiplomaDefenseService {

    private final DiplomaDefenseRepository diplomaDefenseRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public DiplomaDefenseServiceImpl(
            DiplomaDefenseRepository diplomaDefenseRepository,
            TeacherRepository teacherRepository
            ) {
        this.diplomaDefenseRepository = diplomaDefenseRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaDefenseDto> findAll() {
        return diplomaDefenseRepository.findAll()
                .stream()
                .map(this::toDiplomaDefenseDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaDefenseSecurity.isSupervisorOfDefense(#id, @keycloakUtils.getUserId(#auth)))")
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
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    public DiplomaDefenseDto create(
            @Valid CreateDiplomaDefenseDto diplomaDefense,
            Authentication auth) {
        if (diplomaDefense.getDate() == null) {
            throw new DiplomaDefenseValidationException("Date cannot be null");
        }
        DiplomaDefense newDiplomaDefense = new DiplomaDefense();
        newDiplomaDefense.setDate(diplomaDefense.getDate());

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            String keycloakUserId = KeycloakUtils.getUserId(auth);
            Teacher teacher = teacherRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with keycloak user id: " + keycloakUserId));
            newDiplomaDefense.setSupervisor(teacher);
        }
        else {
            newDiplomaDefense.setSupervisor(teacherRepository.findById(diplomaDefense.getSupervisorId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + diplomaDefense.getSupervisorId())));
        }

        try {
            DiplomaDefense savedDiplomaDefense = diplomaDefenseRepository.save(newDiplomaDefense);
            return toDiplomaDefenseDto(savedDiplomaDefense);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException("Unable to create diploma defense due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaDefenseSecurity.isSupervisorOfDefense(#id, @keycloakUtils.getUserId(#auth)))")
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
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaDefenseSecurity.isSupervisorOfDefense(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(
            @NotNull @Positive Long id,
            Authentication auth) {
        diplomaDefenseRepository.findById(id)
                .orElseThrow(() -> new DiplomaDefenseNotFoundException("Diploma defense not found with id: " + id));
        try {
            diplomaDefenseRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DiplomaDefenseCreationException("Unable to delete diploma defense due to data integrity issues", e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Double findAverageNumberOfStudentsDefendedBetweenDates(@NotNull LocalDate startDate,@NotNull LocalDate endDate) {
        return diplomaDefenseRepository.findAverageNumberOfStudentsDefendedBetween(startDate, endDate);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaDefenseSecurity.isSupervisorOfDefense(#defenseId, @keycloakUtils.getUserId(#auth)))")
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
            diplomaDefenseDto.setSupervisorId(diplomaDefense.getSupervisor().getId());
        }
        return diplomaDefenseDto;
    }




}
