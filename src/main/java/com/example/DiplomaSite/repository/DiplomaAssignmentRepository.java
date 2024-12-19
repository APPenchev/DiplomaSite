package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiplomaAssignmentRepository extends JpaRepository<DiplomaAssignment, Long> {

    List<DiplomaAssignment> findByApprovedTrue();

    List<DiplomaAssignment> findByTopicContainingIgnoreCase(String partialTopic);

    List<DiplomaAssignment> findBySupervisorAndApprovedTrue(Teacher supervisor);

}
