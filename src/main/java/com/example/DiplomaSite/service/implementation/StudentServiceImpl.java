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
import com.example.DiplomaSite.service.DiplomaAssignmentService;
import com.example.DiplomaSite.service.StudentService;
import com.example.DiplomaSite.service.validation.StudentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentValidator studentValidator;
    private final DiplomaAssignmentService diplomaAssignmentService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              StudentValidator studentValidator,
                              DiplomaAssignmentService diplomaAssignmentService) {
        this.studentRepository = studentRepository;
        this.studentValidator = studentValidator;
        this.diplomaAssignmentService = diplomaAssignmentService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    public List<StudentDto> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    public List<StudentDto> getStudentsWhoPassedBetweenDates(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        return studentRepository.findStudentsWhoPassedBetweenDates(startDate, endDate)
                .stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin') " +
            "or hasAnyAuthority('teacher') " +
            "or (hasAnyAuthority('student')) and @studentSecurity.isSameStudent(#id, @keycloakUtils.getUserId(#auth))")
    public StudentDto getById(
            @NotNull @Positive Long id,
            Authentication auth) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return toStudentDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    public StudentDto getByFacultyNumber(@NotNull String facultyNumber) {
        Student student = studentRepository.findByFacultyNumber(facultyNumber)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with facultyNumber: " + facultyNumber));
        return toStudentDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    public List<StudentDto> getStudentByName(@NotNull String name) {
        return studentRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public StudentDto create(@Valid CreateStudentDto createStudentDto) {
        studentValidator.validateName(createStudentDto.getName());

        Student student = new Student();
        student.setName(createStudentDto.getName());
        student.setFacultyNumber(generateNextFacultyNumber());

        try {
            Student saved = studentRepository.save(student);
            return toStudentDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new StudentCreationException("Unable to create student due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public StudentDto update(@NotNull @Positive Long id, UpdateStudentDto updateStudentDto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (updateStudentDto.getName() != null) {
            studentValidator.validateName(updateStudentDto.getName());
            existing.setName(updateStudentDto.getName());
        }

        if (updateStudentDto.getFacultyNumber() != null) {
            studentValidator.validateFacultyNumber(updateStudentDto.getFacultyNumber());
            studentRepository.findByFacultyNumber(updateStudentDto.getFacultyNumber())
                    .filter(s -> !s.getId().equals(id))
                    .ifPresent(s -> {
                        throw new StudentValidationException("Faculty number already in use");
                    });
            existing.setFacultyNumber(updateStudentDto.getFacultyNumber());
        }

        if (updateStudentDto.getKeycloakUserId() != null) {
            existing.setKeycloakUserId(updateStudentDto.getKeycloakUserId());
        }

        try {
            Student saved = studentRepository.save(existing);
            return toStudentDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new StudentCreationException("Unable to update student due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public void delete(
            @NotNull @Positive Long id,
            Authentication auth) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (student.getDiplomaAssignment() != null) {
            diplomaAssignmentService.deleteById(student.getDiplomaAssignment().getId(), auth);
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
        if (student.getDiplomaAssignment() != null) {
            dto.setDiplomaAssignmentId(student.getDiplomaAssignment().getId());
        }
        return dto;
    }

    public String generateNextFacultyNumber() {
        String maxFacultyNumber = studentRepository.findMaxFacultyNumber();
        return (maxFacultyNumber == null) ? "100000" : String.valueOf(Integer.parseInt(maxFacultyNumber) + 1);
    }
}
