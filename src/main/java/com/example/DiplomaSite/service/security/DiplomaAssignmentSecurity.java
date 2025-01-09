package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiplomaAssignmentSecurity {

    private final TeacherRepository teacherRepository;

    @Autowired
    public DiplomaAssignmentSecurity(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public boolean isSupervisor(Long teacherId, String keyCloakId) {
        return teacherRepository.findByKeycloakUserId(keyCloakId)
                .map(teacher -> teacher.getId().equals(teacherId))
                .orElse(false);
    }

    public boolean isAssignee(Long StudentId, String keyCloakId) {
        return teacherRepository.findByKeycloakUserId(keyCloakId)
                .map(teacher -> teacher.getId().equals(StudentId))
                .orElse(false);
    }
}
