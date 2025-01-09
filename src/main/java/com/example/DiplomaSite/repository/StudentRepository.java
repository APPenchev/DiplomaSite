package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByFacultyNumber(String facultyNumber);

    // Всички студенти, които са се дипломирали в даден период от време.
    @Query("SELECT DISTINCT r.diplomaThesis.diplomaAssignment.student " +
            "FROM DefenseResult r " +
            "WHERE r.diplomaDefense.date BETWEEN :startDate AND :endDate " +
            "AND r.grade >= :passingGrade")
    List<Student> findGraduatedStudentsBetweenDates(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("passingGrade") Double passingGrade);

    Optional<Student> findByKeycloakUserId(String keycloakUserId);
}
