package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.Review;
import com.example.DiplomaSite.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Броят на студените, които са получили отрицателна рецензия на дипломна
    // работа.
    Long countByPositiveFalse();

    @Query("SELECT r FROM Review r WHERE r.reviewer.id = :teacher_id")
    List<Review> findByReviewer(@Param("teacher_id") Long teacherId);
}
