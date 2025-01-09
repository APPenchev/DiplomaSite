package com.example.DiplomaSite.service;


import com.example.DiplomaSite.configuration.SecurityConfig;
import com.example.DiplomaSite.dto.*;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface UserService {


    String getToken(@Valid UserCredentials userCredentials);

    Set<String> getRole(Authentication auth);

    TeacherDto createTeacher(@Valid CreateUserTeacherDto teacher);

    StudentDto createStudent(@Valid CreateUserStudentDto student);

    void deleteUser(String keycloakId);
}
