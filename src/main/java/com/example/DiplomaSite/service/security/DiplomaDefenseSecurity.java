package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.TeacherNotFoundException;
import com.example.DiplomaSite.repository.DiplomaDefenseRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DiplomaDefenseSecurity {

    private final DiplomaDefenseRepository diplomaDefenseRepository;

    @Autowired
    public DiplomaDefenseSecurity(DiplomaDefenseRepository diplomaDefenseRepository) {
        this.diplomaDefenseRepository = diplomaDefenseRepository;
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

}
