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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DiplomaResultSecurity {

    private final static Logger logger = Logger.getLogger(DiplomaResultSecurity.class.getName());
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
                .map(result -> result.getDiplomaDefense().getDiplomaThesis().getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }

    public boolean isDefenseSupervisor(Long resultId, String keycloakUserId) {

       DefenseResult result = diplomaResultRepository.findById(resultId).orElse(null);
        if (result == null) {
            return false;
        }

        DiplomaDefense defense = result.getDiplomaDefense();
        if (defense == null) {
            return false;
        }

        Teacher supervisor = defense.getSupervisor();
        if (supervisor == null) {
            return false;
        }
        logger.info("Supervisor keycloakUserId: " + supervisor.getKeycloakUserId());
        logger.info("keycloakUserId: " + keycloakUserId);

        return supervisor.getKeycloakUserId().equals(keycloakUserId);
    }

    public boolean isStudentThesis(Long thesisId, String keycloakUserId) {
        return diplomaThesisRepository.findById(thesisId)
                .map(thesis -> thesis.getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }

    public boolean isDefenceTeacherThesis(Long thesisId, String keycloakUserId) {
        return diplomaThesisRepository.findById(thesisId)
                .map(thesis -> thesis.getDiplomaDefenses().stream()
                        .map(DiplomaDefense::getDefenseResult)
                        .flatMap(diplomaResult -> Stream.concat(
                                Stream.ofNullable(diplomaResult.getDiplomaDefense().getSupervisor()),
                                Optional.ofNullable(diplomaResult.getDiplomaDefense().getTeachers())
                                        .orElseGet(Collections::emptyList)
                                        .stream()
                        ))
                        .filter(Objects::nonNull)
                        .anyMatch(teacher -> keycloakUserId.equals(teacher.getKeycloakUserId()))
                )
                .orElse(false);
    }

}
