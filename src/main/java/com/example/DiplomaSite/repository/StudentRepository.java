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

    // Done
    // Всички студенти, които са се дипломирали в даден период от време.

    @Query("""
        SELECT s 
        FROM Student s
        JOIN s.diplomaAssignment da
        JOIN da.diplomaThesis dt
        JOIN dt.diplomaDefenses d
        JOIN d.defenseResult dr
        WHERE dr.grade >= 3.0 AND d.date BETWEEN :startDate AND :endDate
        """)
    List<Student> findStudentsWhoPassedBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    Optional<Student> findByKeycloakUserId(String keycloakUserId);

    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT s FROM Student s WHERE s.facultyNumber = :facultyNumber")
    Optional<Student> findByFacultyNumber(@Param("facultyNumber") String facultyNumber);

    @Query("SELECT MAX(s.facultyNumber) FROM Student s")
    String findMaxFacultyNumber();

    void deleteByKeycloakUserId(String keycloakId);

}
