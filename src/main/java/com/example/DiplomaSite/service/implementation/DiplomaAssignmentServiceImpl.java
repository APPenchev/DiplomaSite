package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateDiplomaAssignmentDto;
import com.example.DiplomaSite.dto.DiplomaAssignmentDto;
import com.example.DiplomaSite.dto.UpdateDiplomaAssignmentDto;
import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.*;
import com.example.DiplomaSite.repository.DiplomaAssignmentRepository;
import com.example.DiplomaSite.repository.StudentRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import com.example.DiplomaSite.service.DiplomaAssignmentService;
import com.example.DiplomaSite.service.validation.DiplomaAssignmentValidator;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class DiplomaAssignmentServiceImpl implements DiplomaAssignmentService {

    private final DiplomaAssignmentRepository diplomaAssignmentRepository;
    private final DiplomaAssignmentValidator diplomaAssignmentValidator;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public DiplomaAssignmentServiceImpl(
            DiplomaAssignmentRepository diplomaAssignmentRepository,
            DiplomaAssignmentValidator diplomaAssignmentValidator,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.diplomaAssignmentRepository = diplomaAssignmentRepository;
        this.diplomaAssignmentValidator = diplomaAssignmentValidator;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaAssignmentDto> findAll() {
        return diplomaAssignmentRepository.findAll()
                .stream()
                .map(this::toDiplomaAssignmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or" +
            "(hasRole('ROLE_TEACHER') and @diplomaAssignmentSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth))) or" +
            "(hasRole('ROLE_STUDENT') and @diplomaAssignmentSecurity.isAssignee(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaAssignmentDto getById(
            @NotNull @Positive Long id,
            Authentication auth
    ) {
        return diplomaAssignmentRepository.findById(id)
                .map(this::toDiplomaAssignmentDto)
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException("Diploma assignment not found with id: " + id));
    }

    @Override
    @Transactional
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    public DiplomaAssignmentDto create(@Valid CreateDiplomaAssignmentDto createDiplomaAssignmentDto) {
        diplomaAssignmentValidator.validateGoal(createDiplomaAssignmentDto.getGoal());
        diplomaAssignmentValidator.validateTasks(createDiplomaAssignmentDto.getTasks());
        diplomaAssignmentValidator.validateTechnologies(createDiplomaAssignmentDto.getTechnologies());
        diplomaAssignmentValidator.validateTopic(createDiplomaAssignmentDto.getTopic());

        DiplomaAssignment diplomaAssignment = new DiplomaAssignment();
        diplomaAssignment.setGoal(createDiplomaAssignmentDto.getGoal());
        diplomaAssignment.setTasks(createDiplomaAssignmentDto.getTasks());
        diplomaAssignment.setTechnologies(createDiplomaAssignmentDto.getTechnologies());
        diplomaAssignment.setTopic(createDiplomaAssignmentDto.getTopic());
        diplomaAssignment.setApproved(createDiplomaAssignmentDto.getApproved());
        Student student = studentRepository.findById(createDiplomaAssignmentDto.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + createDiplomaAssignmentDto.getStudentId()));
        Teacher supervisor = teacherRepository.findById(createDiplomaAssignmentDto.getSupervisorId())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + createDiplomaAssignmentDto.getSupervisorId()));
        diplomaAssignment.setStudent(student);
        diplomaAssignment.setSupervisor(supervisor);

        try {
            DiplomaAssignment savedDiplomaAssignment = diplomaAssignmentRepository.save(diplomaAssignment);
            return toDiplomaAssignmentDto(savedDiplomaAssignment);
        } catch (DiplomaAssignmentCreationException e) {
            throw new DiplomaAssignmentCreationException("Unable to create diploma assignment due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or" +
            "(hasRole('ROLE_TEACHER') and @diplomaAssignmentSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))")
    public DiplomaAssignmentDto update(
            @Positive @NotNull Long id,
            UpdateDiplomaAssignmentDto updateDiplomaAssignmentDto,
            Authentication auth) {
        DiplomaAssignment diplomaAssignment = diplomaAssignmentRepository.findById(id)
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException("Diploma assignment not found with id: " + id));

        if (updateDiplomaAssignmentDto.getGoal() != null) {
            diplomaAssignmentValidator.validateGoal(updateDiplomaAssignmentDto.getGoal());
            diplomaAssignment.setGoal(updateDiplomaAssignmentDto.getGoal());
        }

        if (updateDiplomaAssignmentDto.getTasks() != null) {
            diplomaAssignmentValidator.validateTasks(updateDiplomaAssignmentDto.getTasks());
            diplomaAssignment.setTasks(updateDiplomaAssignmentDto.getTasks());
        }

        if (updateDiplomaAssignmentDto.getTechnologies() != null) {
            diplomaAssignmentValidator.validateTechnologies(updateDiplomaAssignmentDto.getTechnologies());
            diplomaAssignment.setTechnologies(updateDiplomaAssignmentDto.getTechnologies());
        }

        if (updateDiplomaAssignmentDto.getTopic() != null) {
            diplomaAssignmentValidator.validateTopic(updateDiplomaAssignmentDto.getTopic());
            diplomaAssignment.setTopic(updateDiplomaAssignmentDto.getTopic());
        }

        if (updateDiplomaAssignmentDto.getApproved() != null) {
            diplomaAssignment.setApproved(updateDiplomaAssignmentDto.getApproved());
        }

        try {
            DiplomaAssignment updatedDiplomaAssignment = diplomaAssignmentRepository.save(diplomaAssignment);
            return toDiplomaAssignmentDto(updatedDiplomaAssignment);
        } catch (DiplomaAssignmentCreationException e) {
            throw new DiplomaAssignmentCreationException("Unable to update diploma assignment due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or" +
            "(hasRole('ROLE_TEACHER') and @diplomaAssignmentSecurity.isSupervisor(#id, @keycloakUtils.getUserId(#auth)))")
    public void deleteById(@NotNull @Positive Long id) {
        if (!diplomaAssignmentRepository.existsById(id)) {
            throw new DiplomaAssignmentNotFoundException("Diploma assignment not found with id: " + id);
        }

        try {
            diplomaAssignmentRepository.deleteById(id);
        } catch (DiplomaAssignmentCreationException e) {
            throw new DiplomaAssignmentDeletionException("Unable to delete diploma assignment due to data integrity issues", e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaAssignmentDto> findByApprovedTrue() {
        return diplomaAssignmentRepository.findByApprovedTrue()
                .stream()
                .map(this::toDiplomaAssignmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaAssignmentDto> findByTopicContainingIgnoreCase(@NotNull String partialTopic) {
        return diplomaAssignmentRepository.findByTopicContainingIgnoreCase(partialTopic)
                .stream()
                .map(this::toDiplomaAssignmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<DiplomaAssignmentDto> findBySupervisorAndApprovedTrue(@NotNull @Positive Long supervisorId) {
        return diplomaAssignmentRepository.findBySupervisorIdAndApprovedTrue(supervisorId)
                .stream()
                .map(this::toDiplomaAssignmentDto)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or" +
            "(hasRole('ROLE_TEACHER') and @diplomaAssignmentSecurity.isSupervisor(#assignmentId, @keycloakUtils.getUserId(#auth)))")
    public DiplomaAssignmentDto linkAssignmentToStudent(
            @NotNull @Positive Long assignmentId,
            @NotNull @Positive Long studentId,
            Authentication auth) {
        DiplomaAssignment assignment = diplomaAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException(
                        "DiplomaAssignment not found with id: " + assignmentId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));


        assignment.setStudent(student);
        student.setDiplomaAssignment(assignment);

        try {
            assignment = diplomaAssignmentRepository.save(assignment);
            studentRepository.save(student);
        } catch (DiplomaAssignmentCreationException e) {
            throw new DiplomaAssignmentCreationException("Unable to link diploma assignment to student due to data integrity issues", e);
        }


        return toDiplomaAssignmentDto(assignment);
    }

    @Override
    @Transactional
    @RolesAllowed({"ROLE_ADMIN"})
    public DiplomaAssignmentDto linkSupervisor(
            @NotNull @Positive Long assignmentId,
            @NotNull @Positive Long teacherId,
            Authentication auth) {
        DiplomaAssignment assignment = diplomaAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new DiplomaAssignmentNotFoundException(
                        "DiplomaAssignment not found with id: " + assignmentId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(
                        "Teacher not found with id: " + teacherId));


        if (assignment.getSupervisor() != null) {
            assignment.getSupervisor().getSupervisedAssignments().remove(assignment);
        }
        assignment.setSupervisor(teacher);

        if (teacher.getSupervisedAssignments().contains(assignment)) {
            throw new DiplomaAssignmentCreationException("Teacher already supervises this assignment", null);
        }
        teacher.getSupervisedAssignments().add(assignment);

        try {
            assignment = diplomaAssignmentRepository.save(assignment);
            teacherRepository.save(teacher);
        } catch (DiplomaAssignmentCreationException e) {
            throw new DiplomaAssignmentCreationException("Unable to link diploma assignment to teacher due to data integrity issues", e);

        }

        return toDiplomaAssignmentDto(assignment);
    }

    public DiplomaAssignmentDto toDiplomaAssignmentDto(@Valid DiplomaAssignment diplomaAssignment) {
        DiplomaAssignmentDto diplomaAssignmentDto = new DiplomaAssignmentDto();
        diplomaAssignmentDto.setId(diplomaAssignment.getId());
        diplomaAssignmentDto.setTopic(diplomaAssignment.getTopic());
        diplomaAssignmentDto.setGoal(diplomaAssignment.getGoal());
        diplomaAssignmentDto.setTasks(diplomaAssignment.getTasks());
        diplomaAssignmentDto.setTechnologies(diplomaAssignment.getTechnologies());
        diplomaAssignmentDto.setApproved(diplomaAssignment.getApproved());
        if (diplomaAssignment.getStudent() != null) {
            diplomaAssignmentDto.setStudent(diplomaAssignment.getStudent());
        }
        if (diplomaAssignment.getSupervisor() != null) {
            diplomaAssignmentDto.setSupervisor(diplomaAssignment.getSupervisor());
        }
        return diplomaAssignmentDto;
    }
}
