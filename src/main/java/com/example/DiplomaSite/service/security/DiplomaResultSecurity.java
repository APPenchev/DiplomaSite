package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.example.DiplomaSite.repository.DefenseResultRepository;
import com.example.DiplomaSite.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.DiplomaSite.entity.DefenseResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DiplomaResultSecurity {

    private final DefenseResultRepository diplomaResultRepository;
    private final DiplomaThesisRepository diplomaThesisRepository;

    @Autowired
    public DiplomaResultSecurity(
            DefenseResultRepository diplomaResultRepository,
            DiplomaThesisRepository diplomaThesisRepository) {
        this.diplomaResultRepository = diplomaResultRepository;
        this.diplomaThesisRepository = diplomaThesisRepository;
    }

    public boolean isStudent(Long resultId, String keycloakUserId) {
        return diplomaResultRepository.findById(resultId)
                .map(result -> result.getDiplomaThesis().getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }

    public boolean isDefenceTeacher(Long resultId, String keycloakUserId) {
        return diplomaResultRepository.findById(resultId)
                .map(result -> {
                    DiplomaDefense diplomaDefense = result.getDiplomaDefense();
                    return Stream.concat(
                                    Stream.ofNullable(diplomaDefense.getSupervisor()),
                                    Optional.ofNullable(diplomaDefense.getTeachers())
                                            .orElseGet(Collections::emptyList)
                                            .stream()
                            )
                            .anyMatch(teacher -> keycloakUserId.equals(teacher.getKeycloakUserId()));
                })
                .orElse(false);
    }

    public boolean isStudentThesis(Long thesisId, String keycloakUserId) {
        return diplomaThesisRepository.findById(thesisId)
                .map(thesis -> thesis.getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }

    public boolean isDefenceTeacherThesis(Long thesisId, String keycloakUserId) {
        return diplomaThesisRepository.findById(thesisId)
                .map(thesis -> thesis.getDefenseResults().stream()
                        .map(DefenseResult::getDiplomaDefense)
                        .flatMap(diplomaDefense -> Stream.concat(
                                Stream.ofNullable(diplomaDefense.getSupervisor()),
                                Optional.ofNullable(diplomaDefense.getTeachers())
                                        .orElseGet(Collections::emptyList)
                                        .stream()
                        ))
                        .filter(Objects::nonNull)
                        .anyMatch(teacher -> keycloakUserId.equals(teacher.getKeycloakUserId()))
                )
                .orElse(false);
    }

}
