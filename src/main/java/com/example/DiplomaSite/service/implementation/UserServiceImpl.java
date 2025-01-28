package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.*;
import com.example.DiplomaSite.repository.StudentRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import com.example.DiplomaSite.service.StudentService;
import com.example.DiplomaSite.service.TeacherService;
import com.example.DiplomaSite.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private final TeacherService teacherService;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final KeycloakUtils keycloakUtils;

    public UserServiceImpl(
            TeacherService teacherService,
            StudentService studentService,
            KeycloakUtils keycloakUtils,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.keycloakUtils = keycloakUtils;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Token getToken(@Valid UserCredentials userCredentials) {
        return new Token(keycloakUtils.getToken(userCredentials));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRole(Authentication auth) {
        return KeycloakUtils.getUserRoles(auth);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public TeacherDto createTeacher(
            @Valid CreateUserTeacherDto teacher) {
        TeacherDto teacherDto = teacherService.create(teacher.getTeacher());
        String keyCloakId = keycloakUtils.createUser(teacher.getCredentials());
        keycloakUtils.assignClientRole(keyCloakId, "teacher");
        return teacherService.update(teacherDto.getId(), new UpdateTeacherDto(null, null, keyCloakId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public StudentDto createStudent(
            @Valid CreateUserStudentDto student) {
        StudentDto studentDto = studentService.create(student.getStudent());
        String keyCloakId = keycloakUtils.createUser(student.getCredentials());
        keycloakUtils.assignClientRole(keyCloakId, "student");
        return studentService.update(studentDto.getId(), new UpdateStudentDto(null, null, keyCloakId));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public boolean deleteUser(
            @NotBlank String keycloakId) {
        if (studentRepository.findByKeycloakUserId(keycloakId).isPresent()) {
            studentRepository.deleteByKeycloakUserId(keycloakId);
            return keycloakUtils.deleteUser(keycloakId);
        } else if (teacherRepository.findByKeycloakUserId(keycloakId).isPresent()) {
            teacherRepository.deleteByKeycloakUserId(keycloakId);
            return keycloakUtils.deleteUser(keycloakId);
        }

        return false;
    }

}
