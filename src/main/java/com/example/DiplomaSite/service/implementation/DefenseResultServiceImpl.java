package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateDefenseResultDto;
import com.example.DiplomaSite.dto.DefenseResultDto;
import com.example.DiplomaSite.dto.UpdateDefenseResultDto;
import com.example.DiplomaSite.entity.DefenseResult;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.error.DefenseResultCreationException;
import com.example.DiplomaSite.error.DefenseResultNotFoundException;
import com.example.DiplomaSite.error.DiplomaThesisNotFoundException;
import com.example.DiplomaSite.repository.DefenseResultRepository;
import com.example.DiplomaSite.repository.DiplomaDefenseRepository;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.service.DefenseResultService;
import com.example.DiplomaSite.service.validation.DefenseResultValidator;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class DefenseResultServiceImpl implements DefenseResultService {

    private final DefenseResultRepository defenseResultRepository;
    private final DefenseResultValidator defenseResultValidator;
    private final DiplomaThesisRepository diplomaThesisRepository;
    private final DiplomaDefenseRepository diplomaDefenseRepository;

    @Autowired
    public DefenseResultServiceImpl(
            DefenseResultRepository defenseResultRepository,
            DefenseResultValidator defenseResultValidator,
            DiplomaThesisRepository diplomaThesisRepository,
            DiplomaDefenseRepository diplomaDefenseRepository) {
        this.defenseResultRepository = defenseResultRepository;
        this.defenseResultValidator = defenseResultValidator;
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.diplomaDefenseRepository = diplomaDefenseRepository;
    }


    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DefenseResultDto> findAll() {
        return defenseResultRepository.findAll()
                .stream()
                .map(this::toDefenseResultDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacherThesis(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasRole('ROLE_STUDENT') and @diplomaResultSecurity.isStudentThesis(#id, @keycloakUtils.getUserId(#auth)))")
    public List<DefenseResultDto> findAllByThesisId(
            @NotNull @Positive Long thesisId,
            Authentication auth) {
        return defenseResultRepository.findAllByDiplomaThesisId(thesisId)
                .stream()
                .map(this::toDefenseResultDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacher(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasRole('ROLE_STUDENT') and @diplomaResultSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public DefenseResultDto getById(
            @NotNull @Positive Long id,
            Authentication auth) {
        return defenseResultRepository.findById(id)
                .map(this::toDefenseResultDto)
                .orElseThrow(() -> new DefenseResultNotFoundException("Defense result not found with id: " + id));
    }

    @Override
    @Transactional
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    public DefenseResultDto create(
            @Valid CreateDefenseResultDto defenseResult) {
        defenseResultValidator.validateGrade(defenseResult.getGrade());

        DefenseResult newDefenseResult = new DefenseResult();
        newDefenseResult.setGrade(defenseResult.getGrade());
        DiplomaDefense defense = diplomaDefenseRepository.findById(defenseResult.getDefenseId())
                .orElseThrow(() -> new DefenseResultNotFoundException("Diploma defense not found with id: " + defenseResult.getDefenseId()));
        newDefenseResult.setDiplomaDefense(defense);

        try {
            DefenseResult savedDefenseResult = defenseResultRepository.save(newDefenseResult);
            return toDefenseResultDto(savedDefenseResult);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to create review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacher(#id, @keycloakUtils.getUserId(#auth)))")
    public DefenseResultDto update(
            @NotNull @Positive Long id,
            UpdateDefenseResultDto defenseResult,
            Authentication auth
    ) {


        DefenseResult existingDefenseResult = defenseResultRepository.findById(id)
                .orElseThrow(() -> new DefenseResultNotFoundException("Defense result not found with id: " + id));

        if (defenseResult.getGrade() != null) {
            defenseResultValidator.validateGrade(defenseResult.getGrade());
            existingDefenseResult.setGrade(defenseResult.getGrade());
        }

        try {
            DefenseResult saved = defenseResultRepository.save(existingDefenseResult);
            return toDefenseResultDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to update defense result due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacher(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(
            @NotNull @Positive Long id,
            Authentication auth
    ) {
        if (!defenseResultRepository.existsById(id)) {
            throw new DefenseResultNotFoundException("Defense result not found with id: " + id);
        }

        try {
            defenseResultRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to delete defense result due to data integrity issues", e);
        }

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacher(#defenseResultId, @keycloakUtils.getUserId(#auth)))")
    public DefenseResultDto linkResultToThesis(
            @NotNull @Positive Long defenseResultId,
            @NotNull @Positive Long thesisId,
            Authentication auth) {
        DefenseResult result = defenseResultRepository.findById(defenseResultId)
                .orElseThrow(() -> new DefenseResultNotFoundException("Defense result not found with id: " + defenseResultId));
        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + thesisId));


        if (result.getDiplomaThesis() != null) {
            result.getDiplomaThesis().getDefenseResults().remove(result);
        }
        result.setDiplomaThesis(thesis);

        if (thesis.getDefenseResults().contains(result)) {
            throw new DefenseResultCreationException("Defense result is already linked to this thesis", null);
        }
        thesis.getDefenseResults().add(result);

        try {
            result = defenseResultRepository.save(result);
            diplomaThesisRepository.save(thesis);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to link defense result to thesis due to data integrity issues", e);
        }

        return toDefenseResultDto(result);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @diplomaResultSecurity.isDefenceTeacher(#defenseResultId, @keycloakUtils.getUserId(#auth)))")
    public DefenseResultDto linkResultToDefense(
            @NotNull @Positive Long defenseResultId,
            @NotNull @Positive Long diplomaDefenseId,
            Authentication auth) {
        DefenseResult result = defenseResultRepository.findById(defenseResultId)
                .orElseThrow(() -> new DefenseResultNotFoundException("Defense result not found with id: " + defenseResultId));
        DiplomaDefense defense = diplomaDefenseRepository.findById(diplomaDefenseId)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma defense not found with id: " + diplomaDefenseId));


        result.setDiplomaDefense(defense);
        defense.setDefenseResult(result);

        try {
            result = defenseResultRepository.save(result);
            diplomaDefenseRepository.save(defense);
        } catch (DataIntegrityViolationException e) {
            throw new DefenseResultCreationException("Unable to link defense result to defense due to data integrity issues", e);
        }

        return toDefenseResultDto(result);
    }

    public DefenseResultDto toDefenseResultDto(DefenseResult defenseResult) {
        DefenseResultDto defenseResultDto = new DefenseResultDto();
        defenseResultDto.setId(defenseResult.getId());
        defenseResultDto.setGrade(defenseResult.getGrade());
        defenseResultDto.setDefenseId(defenseResult.getDiplomaDefense().getId());
        return defenseResultDto;
    }
}
