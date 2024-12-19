package com.example.DiplomaSite.repository;
import com.example.DiplomaSite.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setKeycloakUserId("123456");
        testStudent.setName("John Doe");
        testStudent.setFacultyNumber("AB123456");
        testStudent = entityManager.persistFlushFind(testStudent);


        testStudent = entityManager.merge(testStudent);
    }

    @Test
    @DisplayName("Find Student by Faculty Number - Exists")
    void testFindByFacultyNumber_Exists() {
        String facultyNumber = "AB123456";

        Optional<Student> foundStudent = studentRepository.findByFacultyNumber(facultyNumber);

        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getFacultyNumber()).isEqualTo(facultyNumber);
    }

    @Test
    @DisplayName("Find Student by Faculty Number - Not Exists")
    void testFindByFacultyNumber_NotExists() {
        Optional<Student> foundStudent = studentRepository.findByFacultyNumber("XY999999");

        assertThat(foundStudent).isNotPresent();
    }
    @Test
    @DisplayName("Find Graduated Students Between Dates")
    void testFindGraduatedStudentsBetweenDates() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(1);
        Double passingGrade = 3.0;

        DiplomaAssignment assignment = new DiplomaAssignment();
        assignment.setGoal("Test Goal");
        assignment.setTopic("Test Topic");
        assignment.setStudent(testStudent);
        entityManager.persistAndFlush(assignment);

        DiplomaThesis thesis = new DiplomaThesis();
        thesis.setTitle("Test Thesis");
        thesis.setText("Test Text");
        thesis.setUploadDate(LocalDate.now());
        thesis.setDiplomaAssignment(assignment);
        entityManager.persistAndFlush(thesis);

        DiplomaDefense defense = new DiplomaDefense();
        defense.setDate(LocalDate.now());
        entityManager.persistAndFlush(defense);

        DefenseResult result = new DefenseResult();
        result.setDiplomaThesis(thesis);
        result.setDiplomaDefense(defense);
        result.setGrade(3.5);

        entityManager.persistAndFlush(result);
        testStudent.setDiplomaAssignment(assignment);

        List<Student> graduatedStudents = studentRepository.findGraduatedStudentsBetweenDates(
                startDate, endDate, passingGrade
        );

        assertThat(graduatedStudents).isNotNull().contains(testStudent);
    }
}
