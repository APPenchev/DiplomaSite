package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.dto.AssignmentStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiplomaAssignmentRepository extends JpaRepository<DiplomaAssignment, Long> {

    // Всички одобрени задания за дипломна работа.
    List<DiplomaAssignment> findByApprovedTrue();

    @Query("SELECT da FROM DiplomaAssignment da WHERE da.student.keycloakUserId = :keycloakUserId")
    Optional<DiplomaAssignment> findByStudentKeycloakId(String keycloakUserId);


    List<DiplomaAssignment> findByTopicContainingIgnoreCase(@Param("topic") String topic);

    // Всички одобрени задания за дипломна работа, на които е ръководител
    // определен преподавател.
    @Query("SELECT da FROM DiplomaAssignment da WHERE da.supervisor.id = :supervisorId AND da.approved = true")
    List<DiplomaAssignment> findBySupervisorIdAndApprovedTrue(@Param("supervisorId") Long supervisorId);

    // Всички теми на дипломни работи, които в заглавието си съдържат определен
    // символен низ.
    @Query("""
       SELECT DISTINCT new com.example.DiplomaSite.dto.AssignmentStatusProjection(
           da.id AS assignmentId,
           da.topic AS topic,
           da.approved AS approved,
           (dt IS NOT NULL) AS hasThesis,
           (r IS NOT NULL) AS thesisReviewed,
           (r.positive = true) AS positiveReview,
           (dd IS NOT NULL) AS hasDefense
       )
       FROM DiplomaAssignment da
       LEFT JOIN da.diplomaThesis dt
       LEFT JOIN dt.review r
       LEFT JOIN dt.diplomaDefenses dd
       WHERE LOWER(da.topic) LIKE LOWER(CONCAT('%', :topic, '%'))
       """)
    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgressByTopic(@Param("topic") String topic);

    @Query("""
       SELECT DISTINCT new com.example.DiplomaSite.dto.AssignmentStatusProjection(
           da.id AS assignmentId,
           da.topic AS topic,
           da.approved AS approved,
           (dt IS NOT NULL) AS hasThesis,
           (r IS NOT NULL) AS thesisReviewed,
           (r.positive = true) AS positiveReview,
           (dd IS NOT NULL) AS hasDefense
       )
       FROM DiplomaAssignment da
       LEFT JOIN da.diplomaThesis dt
       LEFT JOIN dt.review r
       LEFT JOIN dt.diplomaDefenses dd
       """)
    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgress();

    // Всички одобрени задания за дипломна работа, на които е ръководител
    // определен преподавател.
    @Query("""
   SELECT DISTINCT new com.example.DiplomaSite.dto.AssignmentStatusProjection(
       da.id AS assignmentId,
       da.topic AS topic,
       da.approved AS approved,
       (dt IS NOT NULL) AS hasThesis,
       (r IS NOT NULL) AS thesisReviewed,
       (r.positive = true) AS positiveReview,
       (dd IS NOT NULL) AS hasDefense
   )
   FROM DiplomaAssignment da
   LEFT JOIN da.diplomaThesis dt
   LEFT JOIN dt.review r
   LEFT JOIN dt.diplomaDefenses dd
   LEFT JOIN da.supervisor t
   WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :teacherName, '%'))
   """)
    List<AssignmentStatusProjection> getAssignmentStatusAndGradingProgressByTeacher(@Param("teacherName") String teacherName);


}
