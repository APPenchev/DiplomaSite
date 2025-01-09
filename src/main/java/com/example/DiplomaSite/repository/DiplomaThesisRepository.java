package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DiplomaThesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiplomaThesisRepository extends JpaRepository<DiplomaThesis, Long> {

    // Всички дипломни тези, които са оценени с оценка между minGrade и maxGrade.
    @Query("SELECT DISTINCT t FROM DiplomaThesis t JOIN t.defenseResults dr WHERE dr.grade BETWEEN :minGrade AND :maxGrade")
    List<DiplomaThesis> findByGradeBetween(@Param("minGrade") Double minGrade, @Param("maxGrade") Double maxGrade);
}
