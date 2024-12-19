package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.enums.TeacherPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findByNameContainingIgnoreCase(String namePart);

    List<Teacher> findByPosition(TeacherPosition position);

    @Query("SELECT COUNT(r) FROM DefenseResult r " +
            "JOIN r.diplomaThesis dt " +
            "JOIN dt.diplomaAssignment da " +
            "WHERE da.supervisor = :teacher " +
            "AND r.grade >= :passingGrade")
    Long countSuccessfullyGraduatedStudentsForTeacher(@Param("teacher") Teacher teacher,
                                                      @Param("passingGrade") Double passingGrade);



}
