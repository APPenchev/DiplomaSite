package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.enums.TeacherPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findByNameContainingIgnoreCase(String namePart);

    List<Teacher> findByPosition(TeacherPosition position);

    // Броят на защитилите успешно дипломна работа на определен преподавател.
    @Query("SELECT COUNT(r) " +
            "FROM DefenseResult r " +
            "JOIN r.diplomaThesis dt " +
            "JOIN dt.diplomaAssignment da " +
            "WHERE da.supervisor.id = :teacherId " +
            "AND r.grade >= :passingGrade")
    Long countSuccessfullyGraduatedStudentsForTeacher(@Param("teacherId") Long teacherId,
                                                      @Param("passingGrade") Double passingGrade);


    Optional<Teacher> findByKeycloakUserId(String keycloakUserId);
}
