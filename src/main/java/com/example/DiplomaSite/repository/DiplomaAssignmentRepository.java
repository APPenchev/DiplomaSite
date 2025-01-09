package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiplomaAssignmentRepository extends JpaRepository<DiplomaAssignment, Long> {

    // Всички одобрени задания за дипломна работа.
    List<DiplomaAssignment> findByApprovedTrue();

    // Всички теми на дипломни работи, които в заглавието си съдържат определен
    // символен низ.
    List<DiplomaAssignment> findByTopicContainingIgnoreCase(String partialTopic);

    // Всички одобрени задания за дипломна работа, на които е ръководител
    // определен преподавател.
    @Query("SELECT da FROM DiplomaAssignment da WHERE da.supervisor.id = :supervisorId AND da.approved = true")
    List<DiplomaAssignment> findBySupervisorIdAndApprovedTrue(@Param("supervisorId") Long supervisorId);

}
