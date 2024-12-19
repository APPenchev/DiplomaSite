package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DiplomaDefense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface DiplomaDefenseRepository extends JpaRepository<DiplomaDefense, Long> {

    @Query("SELECT AVG(size(d.defenseResults)) FROM DiplomaDefense d " +
            "WHERE d.date BETWEEN :startDate AND :endDate")
    Double findAverageNumberOfStudentsDefendedBetween(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}
