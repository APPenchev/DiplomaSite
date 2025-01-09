package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DiplomaThesisSecurity {

    private final DiplomaThesisRepository diplomaThesisRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public DiplomaThesisSecurity(
            DiplomaThesisRepository diplomaThesisRepository,
            StudentRepository studentRepository) {
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.studentRepository = studentRepository;

    }

    /**
     * Returns true if the current user in the authentication
     * is the supervisor of the specified DiplomaThesis (id).
     */

    public boolean isSupervisor(DiplomaThesis thesis, String keycloakUserId) {
        Teacher supervisor = thesis.getDiplomaAssignment().getSupervisor();
        if (supervisor == null) {
            return false;
        }
        return supervisor.getKeycloakUserId().equals(keycloakUserId);
    }

    /**
     * Returns true if the current user in the authentication
     * is the student of the specified DiplomaThesis (id).
     */

    public boolean isStudent(DiplomaThesis thesis, String keycloakUserId) {
        Student student = thesis.getDiplomaAssignment().getStudent();
        if (student == null) {
            return false;
        }
        return student.getKeycloakUserId().equals(keycloakUserId);

    }

    /**
     * Returns true if the current thesis isn't confidential.
     */
    public boolean isNotConfidential(Long thesisId) {
        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId).orElse(null);
        if (thesis == null) {
            return false;
        }
        return isNotConfidential(thesis);
    }

    public boolean isNotConfidential(DiplomaThesis thesis) {
        return !thesis.getConfidential();
    }


}
