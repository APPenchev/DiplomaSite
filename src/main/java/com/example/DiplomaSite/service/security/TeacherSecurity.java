package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeacherSecurity {

    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherSecurity(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public boolean isSameTeacher(Long teacherId, String keycloakUserId) {
        return teacherRepository.findByKeycloakUserId(keycloakUserId)
                .map(teacher -> teacher.getId().equals(teacherId))
                .orElse(false);
    }
}
