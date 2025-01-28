package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateTeacherDto;
import com.example.DiplomaSite.dto.TeacherDto;
import com.example.DiplomaSite.dto.UpdateTeacherDto;
import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.enums.TeacherPosition;
import com.example.DiplomaSite.error.TeacherCreationException;
import com.example.DiplomaSite.error.TeacherDeletionException;
import com.example.DiplomaSite.error.TeacherNotFoundException;
import com.example.DiplomaSite.repository.TeacherRepository;
import com.example.DiplomaSite.service.DiplomaAssignmentService;
import com.example.DiplomaSite.service.TeacherService;
import com.example.DiplomaSite.service.validation.TeacherValidator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherValidator teacherValidator;
    private final DiplomaAssignmentService diplomaAssignmentService;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository, TeacherValidator teacherValidator, DiplomaAssignmentService diplomaAssignmentService) {
        this.teacherRepository = teacherRepository;
        this.teacherValidator = teacherValidator;
        this.diplomaAssignmentService = diplomaAssignmentService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    public List<TeacherDto> findAll() {
        return teacherRepository.findAll()
                .stream()
                .map(this::toTeacherDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('admin') or " +
            "(hasAuthority('teacher') and @teacherSecurity.isSameTeacher(#id, @keycloakUtils.getUserId(#auth)))")
    public TeacherDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    ) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id));
        return toTeacherDto(teacher);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAuthority('teacher') and @teacherSecurity.isSameTeacherK(#keycloakId, @keycloakUtils.getUserId(#auth)))")
    public TeacherDto findTeacherIdByKeycloakId(@NotNull String keycloakId,
                                                Authentication auth) {
        return teacherRepository.findByKeycloakUserId(keycloakId)
                .map(this::toTeacherDto)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with keycloak id: " + keycloakId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public TeacherDto create(@Valid CreateTeacherDto createTeacherDto) {
       teacherValidator.validateName(createTeacherDto.getName());

       Teacher teacher = new Teacher();
       teacher.setName(createTeacherDto.getName());
       teacher.setPosition(createTeacherDto.getPosition());

       try {
           Teacher saved = teacherRepository.save(teacher);
           return toTeacherDto(saved);
       } catch (DataIntegrityViolationException e) {
           throw new TeacherCreationException("Unable to create teacher due to data integrity issues", e);
       }

    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public TeacherDto update(@Positive @NotNull Long id,  UpdateTeacherDto teacher) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id));

        if (teacher.getName() != null) {
            teacherValidator.validateName(teacher.getName());
            existing.setName(teacher.getName());
        }

        if (teacher.getPosition() != null) {
            existing.setPosition(teacher.getPosition());
        }

        if (teacher.getKeycloakUserId() != null) {
            // validate keycloak user id
            existing.setKeycloakUserId(teacher.getKeycloakUserId());
        }

        try {
            Teacher saved = teacherRepository.save(existing);
            return toTeacherDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new TeacherCreationException("Unable to update teacher due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public void delete(
            @Positive @NotNull Long id,
            Authentication auth) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id));

        if (!teacher.getSupervisedAssignments().isEmpty()) {
            for (DiplomaAssignment assignment : teacher.getSupervisedAssignments()) {
                diplomaAssignmentService.deleteById(assignment.getId(), auth);
            }
        }

        teacherRepository.flush();

        try {
            teacherRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new TeacherDeletionException("Unable to delete teacher due to existing dependencies", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<TeacherDto> getTeachersByName(@NotNull String namePart) {
        return teacherRepository.findByNameContainingIgnoreCase(namePart)
                .stream()
                .map(this::toTeacherDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<TeacherDto> getTeachersByPosition(@NotNull TeacherPosition position) {
        return teacherRepository.findByPosition(position)
                .stream()
                .map(this::toTeacherDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    @PreAuthorize("hasAuthority('admin') or (hasAuthority('teacher') and @teacherSecurity.isSameTeacher(#teacherId, @keycloakUtils.getUserId(#auth)))")
    public Long countSuccessfullyGraduatedStudentsForTeacher(
            @NotNull @Positive Long teacherId,
            @NotNull @Positive Double passingGrade,
            Authentication auth) {
        return teacherRepository.countSuccessfullyGraduatedStudentsForTeacher(teacherId, passingGrade);
    }

    private TeacherDto toTeacherDto(Teacher teacher) {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacher.getId());
        teacherDto.setName(teacher.getName());
        teacherDto.setPosition(teacher.getPosition());
        teacherDto.setKeycloakUserId(teacher.getKeycloakUserId());
        return teacherDto;
    }
}
