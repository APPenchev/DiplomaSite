package com.example.DiplomaSite.service.security;

import com.example.DiplomaSite.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentSecurity {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentSecurity(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public boolean isSameStudent(Long studentId, String keycloakUserId) {
        return studentRepository.findByKeycloakUserId(keycloakUserId)
                .map(student -> student.getId().equals(studentId))
                .orElse(false);
    }

}
