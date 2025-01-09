package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.CreateDiplomaThesisDto;
import com.example.DiplomaSite.dto.DiplomaThesisDto;
import com.example.DiplomaSite.dto.UpdateDiplomaThesisDto;
import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.error.DiplomaAssignmentNotFoundException;
import com.example.DiplomaSite.error.DiplomaThesisCreationException;
import com.example.DiplomaSite.error.DiplomaThesisNotFoundException;
import com.example.DiplomaSite.repository.DiplomaAssignmentRepository;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.service.DiplomaThesisService;
import com.example.DiplomaSite.service.security.DiplomaThesisSecurity;
import com.example.DiplomaSite.service.validation.DiplomaThesisValidator;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Set;

@Service
@Validated
public class DiplomaThesisServiceImpl implements DiplomaThesisService {

    private final DiplomaThesisRepository diplomaThesisRepository;
    private final DiplomaThesisValidator diplomaThesisValidator;
    private final DiplomaAssignmentRepository diplomaAssignmentRepository;
    private final DiplomaThesisSecurity diplomaThesisSecurity;

    @Autowired
    public DiplomaThesisServiceImpl(
            DiplomaThesisRepository diplomaThesisRepository,
            DiplomaThesisValidator diplomaThesisValidator,
            DiplomaAssignmentRepository diplomaAssignmentRepository,
            DiplomaThesisSecurity diplomaThesisSecurity) {
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.diplomaThesisValidator = diplomaThesisValidator;
        this.diplomaAssignmentRepository = diplomaAssignmentRepository;
        this.diplomaThesisSecurity = diplomaThesisSecurity;

    }


    @Override
    @Transactional(readOnly = true)
    public List<DiplomaThesisDto> findAll(Authentication authentication) {
        List<DiplomaThesis> allTheses = diplomaThesisRepository.findAll();
        Set<String> roles = KeycloakUtils.getUserRoles(authentication);
        String currentUserId = KeycloakUtils.getUserId(authentication);
        List<DiplomaThesis> filtered = allTheses.stream()
                .filter(thesis -> {
                    boolean notConfidential = diplomaThesisSecurity.isNotConfidential(thesis);

                    if (roles.contains("ADMIN") || notConfidential) {
                        return true;
                    }
                    else if (roles.contains("STUDENT")) {
                        return diplomaThesisSecurity.isStudent(thesis, currentUserId);
                    }
                    else if (roles.contains("TEACHER")) {
                        return diplomaThesisSecurity.isSupervisor(thesis, currentUserId);
                    }
                    return false;
                })
                .toList();

        return filtered.stream()
                .map(this::toDiplomaThesisDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize(
            "hasRole('ADMIN')"
                    + " or (hasRole('TEACHER') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth))"
                    + " or (hasRole('STUDENT') and @diplomaThesisSecurity.isNotConfidential(#id))"
                    + " or @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)"
    )
    public DiplomaThesisDto getById(@NotNull @Positive Long id,
                                    Authentication auth) {
        return diplomaThesisRepository.findById(id)
                .map(this::toDiplomaThesisDto)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') or hasRole('STUDENT'))")
    public DiplomaThesisDto create(
            @Valid CreateDiplomaThesisDto diplomaThesis) {
        diplomaThesisValidator.validateTitle(diplomaThesis.getTitle());
        diplomaThesisValidator.validateText(diplomaThesis.getText());

        DiplomaThesis newDiplomaThesis = new DiplomaThesis();
        newDiplomaThesis.setTitle(diplomaThesis.getTitle());
        newDiplomaThesis.setText(diplomaThesis.getText());
        newDiplomaThesis.setUploadDate(diplomaThesis.getUploadDate());
        newDiplomaThesis.setConfidential(diplomaThesis.getConfidential());
        try {
            DiplomaThesis savedDiplomaThesis = diplomaThesisRepository.save(newDiplomaThesis);
            return toDiplomaThesisDto(savedDiplomaThesis);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to create diploma thesis due to data integrity issues", e);
        }

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasRole('STUDENT') and @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaThesisDto update(
            @NotNull @Positive Long id,
            UpdateDiplomaThesisDto diplomaThesisDto,
            Authentication auth) {

        DiplomaThesis diplomaThesis = diplomaThesisRepository.findById(id)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + id));

        if (diplomaThesisDto.getTitle() != null) {
            diplomaThesisValidator.validateTitle(diplomaThesisDto.getTitle());
            diplomaThesis.setTitle(diplomaThesisDto.getTitle());
        }
        if (diplomaThesisDto.getText() != null) {
            diplomaThesisValidator.validateText(diplomaThesisDto.getText());
            diplomaThesis.setText(diplomaThesisDto.getText());
        }
        if (diplomaThesisDto.getUploadDate() != null) {
            diplomaThesis.setUploadDate(diplomaThesisDto.getUploadDate());
        }
        if (diplomaThesisDto.getConfidential() != null) {
            diplomaThesis.setConfidential(diplomaThesisDto.getConfidential());
        }
        try {
            diplomaThesis = diplomaThesisRepository.save(diplomaThesis);
            return toDiplomaThesisDto(diplomaThesis);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to update diploma thesis due to data integrity issues", e);
        }

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(
            @NotNull @Positive Long id,
            Authentication authentication) {
        diplomaThesisRepository.findById(id)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + id));

        try {
            diplomaThesisRepository.deleteById(id);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to delete diploma thesis due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaThesisDto> findByGradeBetween(
            @NotNull @Positive Double minGrade,
            @NotNull @Positive Double maxGrade) {
        return diplomaThesisRepository.findByGradeBetween(minGrade, maxGrade)
                .stream()
                .map(this::toDiplomaThesisDto)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('TEACHER') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))" +
            "(hasRole('STUDENT') and @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaThesisDto linkThesisToAssignment(
            @NotNull @Positive Long thesisId,
            @NotNull @Positive Long assignmentId,
            Authentication authentication) {
        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + thesisId));
        DiplomaAssignment assignment = diplomaAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException("Diploma assignment not found with id: " + assignmentId));


        thesis.setDiplomaAssignment(assignment);
        assignment.setDiplomaThesis(thesis);

        try {
            thesis = diplomaThesisRepository.save(thesis);
            diplomaAssignmentRepository.save(assignment);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to link diploma thesis to diploma assignment due to data integrity issues", e);
        }


        return toDiplomaThesisDto(thesis);
    }

    public DiplomaThesisDto toDiplomaThesisDto(DiplomaThesis diplomaThesis) {
        DiplomaThesisDto diplomaThesisDto = new DiplomaThesisDto();
        diplomaThesisDto.setId(diplomaThesis.getId());
        diplomaThesisDto.setTitle(diplomaThesis.getTitle());
        diplomaThesisDto.setText(diplomaThesis.getText());
        diplomaThesisDto.setUploadDate(diplomaThesis.getUploadDate());
        diplomaThesisDto.setConfidential(diplomaThesis.getConfidential());
        return diplomaThesisDto;
    }



}
