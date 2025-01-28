package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.TeacherNotFoundException;
import com.example.DiplomaSite.repository.DiplomaDefenseRepository;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DiplomaDefenseSecurity {

    private final DiplomaDefenseRepository diplomaDefenseRepository;
    private final DiplomaThesisRepository diplomaThesisRepository;

    @Autowired
    public DiplomaDefenseSecurity(DiplomaDefenseRepository diplomaDefenseRepository, DiplomaThesisRepository diplomaThesisRepository) {
        this.diplomaDefenseRepository = diplomaDefenseRepository;
        this.diplomaThesisRepository = diplomaThesisRepository;
    }

    /**
     * Returns true if the current user in the authentication
     * is the supervisor of the specified DiplomaDefense (id).
     */
    public boolean isSupervisor(Long defenseId, String keycloakUserId) {

        DiplomaDefense defense = diplomaDefenseRepository.findById(defenseId).orElse(null);
        if (defense == null) {
            return false;
        }

        Teacher supervisor = defense.getSupervisor();
        if (supervisor == null) {
            throw new TeacherNotFoundException("Supervisor not found");
        }
        return supervisor.getKeycloakUserId().equals(keycloakUserId);
    }

    /**
     * Returns true if the current user in the authentication
     * is the student of the specified DiplomaDefense (id).
     */
    public boolean isStudent(Long defenseId, String keycloakUserId) {

        DiplomaDefense defense = diplomaDefenseRepository.findById(defenseId).orElse(null);
        if (defense == null) {
            return false;
        }

        return defense.getDiplomaThesis().getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId);
    }

    /**
     * Returns true if the current user in the authentication
     * is the student of the specified DiplomaDefense (id).
     */
    public boolean isStudentThesis(Long thesisId, String keycloakUserId) {

        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId).orElse(null);
        if (thesis == null) {
            return false;
        }


        return thesis.getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId);
    }


}
