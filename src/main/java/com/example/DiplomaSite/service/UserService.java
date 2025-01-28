package com.example.DiplomaSite.service;


import com.example.DiplomaSite.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface UserService {


    Token getToken(@Valid UserCredentials userCredentials);

    Set<String> getRole(Authentication auth);

    TeacherDto createTeacher(@Valid CreateUserTeacherDto teacher);

    StudentDto createStudent(@Valid CreateUserStudentDto student);

    boolean deleteUser(@NotBlank String keycloakId);
}
