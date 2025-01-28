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
import com.example.DiplomaSite.repository.ReviewRepository;
import com.example.DiplomaSite.service.DiplomaThesisService;
import com.example.DiplomaSite.service.ReviewService;
import com.example.DiplomaSite.service.security.DiplomaThesisSecurity;
import com.example.DiplomaSite.service.validation.DiplomaThesisValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Validated
public class DiplomaThesisServiceImpl implements DiplomaThesisService {

    private final DiplomaThesisRepository diplomaThesisRepository;
    private final DiplomaThesisValidator diplomaThesisValidator;
    private final DiplomaAssignmentRepository diplomaAssignmentRepository;
    private final DiplomaThesisSecurity diplomaThesisSecurity;
    private final ReviewService reviewService;

    @Autowired
    public DiplomaThesisServiceImpl(
            DiplomaThesisRepository diplomaThesisRepository,
            DiplomaThesisValidator diplomaThesisValidator,
            DiplomaAssignmentRepository diplomaAssignmentRepository,
            DiplomaThesisSecurity diplomaThesisSecurity,
            ReviewService reviewService) {
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.diplomaThesisValidator = diplomaThesisValidator;
        this.diplomaAssignmentRepository = diplomaAssignmentRepository;
        this.diplomaThesisSecurity = diplomaThesisSecurity;
        this.reviewService = reviewService;

    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin', 'student', 'teacher')")
    public List<DiplomaThesisDto> findAll(Authentication authentication) {
        List<DiplomaThesis> allTheses = diplomaThesisRepository.findAll();
        Set<String> roles = KeycloakUtils.getUserRoles(authentication);
        String currentUserId = KeycloakUtils.getUserId(authentication);
        List<DiplomaThesis> filtered = allTheses.stream()
                .filter(thesis -> {
                    boolean notConfidential = diplomaThesisSecurity.isNotConfidential(thesis);

                    if (roles.contains("admin") || notConfidential) {
                        return true;
                    }
                    else if (roles.contains("student")) {
                        return diplomaThesisSecurity.isStudent(thesis.getId(), currentUserId);
                    }
                    else if (roles.contains("teacher")) {
                        return diplomaThesisSecurity.isSupervisor(thesis.getId(), currentUserId);
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
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "hasAnyAuthority('teacher')  or " +
            "(hasAnyAuthority('student') and @diplomaThesisSecurity.isNotConfidential(#id)) or " +
            "(hasAnyAuthority('student') and @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaThesisDto getById(@NotNull @Positive Long id,
                                    Authentication auth) {
        return diplomaThesisRepository.findById(id)
                .map(this::toDiplomaThesisDto)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + id));
    }

    public DiplomaThesisDto getByToken(Authentication auth) {
        return diplomaThesisRepository.findByStudentKeycloakId(KeycloakUtils.getUserId(auth))
                .map(this::toDiplomaThesisDto)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found for user with id: " + KeycloakUtils.getUserId(auth)));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public DiplomaThesisDto create(
            @Valid CreateDiplomaThesisDto diplomaThesis,
            Authentication auth) {

        diplomaThesisValidator.validateTitle(diplomaThesis.getTitle());
        diplomaThesisValidator.validateText(diplomaThesis.getText());

        DiplomaThesis newDiplomaThesis = new DiplomaThesis();
        newDiplomaThesis.setTitle(diplomaThesis.getTitle());
        newDiplomaThesis.setText(diplomaThesis.getText());
        newDiplomaThesis.setUploadDate(diplomaThesis.getUploadDate());
        newDiplomaThesis.setConfidential(true);

        // Fetch the assignment
        DiplomaAssignment assignment = diplomaAssignmentRepository.findById(diplomaThesis.getAssignmentId())
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException("Diploma assignment not found with id: " + diplomaThesis.getAssignmentId()));
        newDiplomaThesis.setDiplomaAssignment(assignment);

        // Check if an existing thesis for the same student exists
        Optional<DiplomaThesis> existingThesis = diplomaThesisRepository.findByStudentKeycloakId(assignment.getStudent().getKeycloakUserId());
        if (existingThesis.isPresent()) {
            DiplomaThesis previousThesis = existingThesis.get();

            // Check if the thesis has been reviewed negatively
            if (previousThesis.getReview() != null && Boolean.FALSE.equals(previousThesis.getReview().getPositive())) {
                // Step 1: Delete the review associated with the old thesis
                reviewService.delete(previousThesis.getReview().getId(), auth);

                // Step 2: Nullify the relationship between the old thesis and the assignment
                previousThesis.setDiplomaAssignment(null);
                diplomaThesisRepository.save(previousThesis);

                // Step 3: Flush the changes to ensure the database reflects the removal of the assignment reference
                diplomaThesisRepository.flush();

                // Step 4: Delete the old thesis
                diplomaThesisRepository.delete(previousThesis);

                // Step 5: Flush again to ensure the thesis row is completely removed
                diplomaThesisRepository.flush();
            } else {
                // If the thesis hasn't been reviewed negatively, throw an exception
                throw new DiplomaThesisCreationException("A valid thesis already exists for this student and cannot be resubmitted.", null);
            }
        }

        try {
            // Save the new thesis
            DiplomaThesis savedDiplomaThesis = diplomaThesisRepository.save(newDiplomaThesis);
            return toDiplomaThesisDto(savedDiplomaThesis);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to create diploma thesis due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasAnyAuthority('student') and @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)) and @diplomaThesisSecurity.isNotReviewed(#id))")
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
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaThesisSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))" +
            "(hasAnyAuthority('student') and @diplomaThesisSecurity.isStudent(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(
            @NotNull @Positive Long id,
            Authentication authentication) {
        DiplomaThesis thesis = diplomaThesisRepository.findById(id)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + id));
        if (thesis.getReview() != null)
            reviewService.delete(thesis.getReview().getId(), authentication);
        if (thesis.getDiplomaAssignment() != null) {
            thesis.getDiplomaAssignment().setDiplomaThesis(null);
            diplomaAssignmentRepository.save(thesis.getDiplomaAssignment());
        }
        try {
            diplomaThesisRepository.deleteById(id);
        } catch (Exception e) {
            throw new DiplomaThesisCreationException("Unable to delete diploma thesis due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
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
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAnyAuthority('teacher') and @diplomaThesisSecurity.isSupervisor(#thesisId, @keycloakUtils.getUserId(#auth))) or " +
            "(hasAnyAuthority('student') and @diplomaThesisSecurity.isStudent(#thesisId, @keycloakUtils.getUserId(#auth)))")
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
        if (diplomaThesis.getDiplomaAssignment() != null) {
            diplomaThesisDto.setAssignmentId(diplomaThesis.getDiplomaAssignment().getId());
            if (diplomaThesis.getDiplomaAssignment().getStudent() != null) {
                diplomaThesisDto.setStudentKeycloakId(diplomaThesis.getDiplomaAssignment().getStudent().getKeycloakUserId());
            }
            if (diplomaThesis.getDiplomaAssignment().getSupervisor() != null) {
                diplomaThesisDto.setTeacherKeycloakId(diplomaThesis.getDiplomaAssignment().getSupervisor().getKeycloakUserId());
            }

        }
        if (diplomaThesis.getReview() != null) {
            diplomaThesisDto.setReviewId(diplomaThesis.getReview().getId());
        }
        return diplomaThesisDto;
    }



}
