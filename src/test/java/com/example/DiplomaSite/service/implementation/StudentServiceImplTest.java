package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.repository.StudentRepository;

import com.example.DiplomaSite.service.validation.StudentValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.DiplomaSite.error.StudentValidationException;
import com.example.DiplomaSite.error.StudentNotFoundException;


import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentValidator studentValidator;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student testStudent;
    private StudentDto testStudentDto;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("John Doe");
        testStudent.setFacultyNumber("AB123456");
        testStudent.setKeycloakUserId("keycloak-123");

        // Create corresponding DTO
        testStudentDto = new StudentDto();
        testStudentDto.setId(1L);
        testStudentDto.setName("John Doe");
        testStudentDto.setFacultyNumber("AB123456");
        testStudentDto.setKeycloakUserId("keycloak-123");
    }

    @Test
    @DisplayName("Get Student by ID - Exists")
    void testGetById_Exists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        StudentDto foundStudent = studentService.getById(1L);

        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getId()).isEqualTo(1L);
        assertThat(foundStudent.getName()).isEqualTo("John Doe");
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Get Student by ID - Not Found")
    void testGetById_NotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getById(1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student not found with id: 1");
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Create Student - Success")
    void testCreate_Success() {
        CreateStudentDto createDto = new CreateStudentDto();
        createDto.setName("John Doe");
        createDto.setFacultyNumber("AB123456");
        createDto.setKeycloakUserId("keycloak-123");

        // Validator calls
        doNothing().when(studentValidator).validateFacultyNumber("AB123456");
        doNothing().when(studentValidator).validateName("John Doe");

        when(studentRepository.findByFacultyNumber("AB123456")).thenReturn(Optional.empty());
        when(studentRepository.save(Mockito.any(Student.class))).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            saved.setId(1L); // simulate DB auto-assigned ID
            return saved;
        });

        StudentDto createdStudent = studentService.create(createDto);

        assertThat(createdStudent).isNotNull();
        assertThat(createdStudent.getId()).isEqualTo(1L);
        assertThat(createdStudent.getFacultyNumber()).isEqualTo("AB123456");
        verify(studentValidator).validateFacultyNumber("AB123456");
        verify(studentValidator).validateName("John Doe");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Create Student - Duplicate Faculty Number")
    void testCreate_DuplicateFacultyNumber() {
        CreateStudentDto createDto = new CreateStudentDto();
        createDto.setName("John Doe");
        createDto.setFacultyNumber("AB123456");
        createDto.setKeycloakUserId("keycloak-123");

        doNothing().when(studentValidator).validateFacultyNumber("AB123456");
        doNothing().when(studentValidator).validateName("John Doe");
        when(studentRepository.findByFacultyNumber("AB123456")).thenReturn(Optional.of(new Student()));

        assertThatThrownBy(() -> studentService.create(createDto))
                .isInstanceOf(StudentValidationException.class)
                .hasMessageContaining("Faculty number already exists");

        verify(studentValidator).validateFacultyNumber("AB123456");
        verify(studentValidator).validateName("John Doe");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Update Student - Success")
    void testUpdate_Success() {
        UpdateStudentDto updateDto = new UpdateStudentDto();
        updateDto.setName("Jane Doe");
        updateDto.setFacultyNumber("AB654321");
        updateDto.setKeycloakUserId("keycloak-456");

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Old Name");
        existingStudent.setFacultyNumber("AB123456");
        existingStudent.setKeycloakUserId("keycloak-123");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        doNothing().when(studentValidator).validateFacultyNumber("AB654321");
        doNothing().when(studentValidator).validateName("Jane Doe");
        when(studentRepository.findByFacultyNumber("AB654321")).thenReturn(Optional.empty());

        when(studentRepository.save(existingStudent)).thenReturn(existingStudent);

        StudentDto updatedStudent = studentService.update(1L, updateDto);

        assertThat(updatedStudent).isNotNull();
        assertThat(updatedStudent.getName()).isEqualTo("Jane Doe");
        assertThat(updatedStudent.getFacultyNumber()).isEqualTo("AB654321");
        assertThat(updatedStudent.getKeycloakUserId()).isEqualTo("keycloak-456");

        verify(studentRepository).findById(1L);
        verify(studentValidator).validateFacultyNumber("AB654321");
        verify(studentValidator).validateName("Jane Doe");
        verify(studentRepository).save(existingStudent);
    }

    @Test
    @DisplayName("Delete Student - Success")
    void testDeleteById_Success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteById(1L);

        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete Student - Not Found")
    void testDeleteById_NotFound() {
        when(studentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteById(1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student not found with id: 1");

        verify(studentRepository).existsById(1L);
        verify(studentRepository, never()).deleteById(1L);
    }
}


