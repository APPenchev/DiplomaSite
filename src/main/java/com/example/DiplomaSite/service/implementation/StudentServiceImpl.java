package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateStudentDto;
import com.example.DiplomaSite.dto.StudentDto;
import com.example.DiplomaSite.dto.UpdateStudentDto;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.error.StudentCreationException;
import com.example.DiplomaSite.error.StudentDeletionException;
import com.example.DiplomaSite.error.StudentNotFoundException;
import com.example.DiplomaSite.error.StudentValidationException;
import com.example.DiplomaSite.repository.StudentRepository;
import com.example.DiplomaSite.service.StudentService;
import com.example.DiplomaSite.service.validation.StudentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentValidator studentValidator;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              StudentValidator studentValidator) {
        this.studentRepository = studentRepository;
        this.studentValidator = studentValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getById(@NotNull @Positive Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return toStudentDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getByFacultyNumber(@NotNull String facultyNumber) {
        studentValidator.validateFacultyNumber(facultyNumber);
        Student student = studentRepository.findByFacultyNumber(facultyNumber)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with facultyNumber: " + facultyNumber));
        return toStudentDto(student);
    }

    @Override
    @Transactional
    public StudentDto create(@Valid CreateStudentDto createStudentDto) {
        studentValidator.validateFacultyNumber(createStudentDto.getFacultyNumber());
        studentValidator.validateName(createStudentDto.getName());

        if (studentRepository.findByFacultyNumber(createStudentDto.getFacultyNumber()).isPresent()) {
            throw new StudentValidationException("Faculty number already exists");
        }

        Student student = new Student();
        student.setName(createStudentDto.getName());
        student.setFacultyNumber(createStudentDto.getFacultyNumber());
        student.setKeycloakUserId(createStudentDto.getKeycloakUserId());

        try {
            Student saved = studentRepository.save(student);
            return toStudentDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new StudentCreationException("Unable to create student due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    public StudentDto update(@NotNull @Positive Long id, @Valid UpdateStudentDto updateStudentDto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (updateStudentDto.getName() != null) {
            studentValidator.validateName(updateStudentDto.getName());
            existing.setName(updateStudentDto.getName());
        }

        if (updateStudentDto.getFacultyNumber() != null) {
            studentValidator.validateFacultyNumber(updateStudentDto.getFacultyNumber());
            // Ensure faculty number uniqueness
            studentRepository.findByFacultyNumber(updateStudentDto.getFacultyNumber())
                    .filter(s -> !s.getId().equals(id))
                    .ifPresent(s -> {
                        throw new StudentValidationException("Faculty number already in use");
                    });
            existing.setFacultyNumber(updateStudentDto.getFacultyNumber());
        }

        if (updateStudentDto.getKeycloakUserId() != null) {
            // If you have any validation for keycloakUserId, do it here
            existing.setKeycloakUserId(updateStudentDto.getKeycloakUserId());
        }

        Student saved = studentRepository.save(existing);
        return toStudentDto(saved);
    }

    @Override
    @Transactional
    public void deleteById(@NotNull @Positive Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }

        try {
            studentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new StudentDeletionException("Unable to delete student due to existing dependencies", e);
        }
    }

    private StudentDto toStudentDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setFacultyNumber(student.getFacultyNumber());
        dto.setKeycloakUserId(student.getKeycloakUserId());
        return dto;
    }
}
