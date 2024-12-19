package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.Student;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository {

    Long countByPositiveFalse();

    //@Query("SELECT r.diplomaThesis.diplomaAssignment.student FROM Review r WHERE r.positive = false")
    //List<Student> findStudentsWithNegativeReviews();

    List<Student> findByPositiveFalse();
}
