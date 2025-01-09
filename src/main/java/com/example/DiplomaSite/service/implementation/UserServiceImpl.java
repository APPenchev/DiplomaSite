package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.configuration.KeycloakUtils;
import com.example.DiplomaSite.dto.*;
import com.example.DiplomaSite.service.StudentService;
import com.example.DiplomaSite.service.TeacherService;
import com.example.DiplomaSite.service.UserService;
import jakarta.validation.Valid;
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
    private final KeycloakUtils keycloakUtils;

    public UserServiceImpl(TeacherService teacherService, StudentService studentService, KeycloakUtils keycloakUtils) {
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.keycloakUtils = keycloakUtils;
    }


    @Override
    @Transactional(readOnly = true)
    public String getToken(@Valid UserCredentials userCredentials) {
        return keycloakUtils.getToken(userCredentials);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRole(Authentication auth) {
        return KeycloakUtils.getUserRoles(auth);
    }

    @Override
    @Transactional
    public TeacherDto createTeacher(@Valid CreateUserTeacherDto teacher) {
        String keyCloakId = keycloakUtils.createUser(teacher.getCredentials());
        TeacherDto teacherDto = teacherService.create(teacher.getTeacher());
        return teacherService.update(teacherDto.getId(), new UpdateTeacherDto(null, null, keyCloakId));
    }

    @Override
    @Transactional
    public StudentDto createStudent(@Valid CreateUserStudentDto student) {
        String keyCloakId = keycloakUtils.createUser(student.getCredentials());
        StudentDto studentDto = studentService.create(student.getStudent());
        return studentService.update(studentDto.getId(), new UpdateStudentDto(null, null, keyCloakId));
    }

    @Override
    @Transactional
    public void deleteUser(String keycloakId) {

    }

}
