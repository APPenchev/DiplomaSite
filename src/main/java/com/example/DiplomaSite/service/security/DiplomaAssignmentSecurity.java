package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.repository.DiplomaAssignmentRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DiplomaAssignmentSecurity {

    private final DiplomaAssignmentRepository diplomaAssignmentRepository;

    private static final Logger logger = Logger.getLogger(DiplomaAssignmentSecurity.class.getName());
    @Autowired
    public DiplomaAssignmentSecurity(DiplomaAssignmentRepository diplomaAssignmentRepository) {
        this.diplomaAssignmentRepository = diplomaAssignmentRepository;
    }

    public boolean isSupervisor(Long diploma, String keyCloakId) {

        return diplomaAssignmentRepository.findById(diploma)
                .map(DiplomaAssignment::getSupervisor)
                .map(Teacher::getKeycloakUserId)
                .map(keyCloakId::equals)
                .orElse(false);

    }

    public boolean isAssignee(Long diploma, String keyCloakId) {

        return diplomaAssignmentRepository.findById(diploma)
                .map(DiplomaAssignment::getStudent)
                .map(Student::getKeycloakUserId)
                .map(keyCloakId::equals)
                .orElse(false);
    }
}
