package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DefenseResult;
import com.example.DiplomaSite.entity.DiplomaDefense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiplomaDefenseRepository extends JpaRepository<DiplomaDefense, Long> {

    // Средният брой на явилите се на дипломна защита студенти в рамките на
    // опреден период.
    @Query("SELECT AVG(CASE WHEN d.defenseResult IS NOT NULL THEN 1 ELSE 0 END) " +
            "FROM DiplomaDefense d WHERE d.date BETWEEN :startDate AND :endDate")
    Double findAverageNumberOfStudentsDefendedBetween(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    List<DiplomaDefense> findAllByDiplomaThesisId(Long thesisId);
}
