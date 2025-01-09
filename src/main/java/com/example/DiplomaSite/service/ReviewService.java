package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateReviewDto;
import com.example.DiplomaSite.dto.ReviewDto;
import com.example.DiplomaSite.dto.UpdateReviewDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReviewService {

    List<ReviewDto> findAll();
    List<ReviewDto> getReviewsByTeacher(Long teacherId, Authentication auth);
    ReviewDto getById(Long id, Authentication auth);
    ReviewDto create(CreateReviewDto review);
    ReviewDto update(Long id, UpdateReviewDto review, Authentication auth);
    Long countApprovedReviews();
    void delete(Long id, Authentication auth);



    ReviewDto linkReviewToTeacher(Long reviewId, Long teacherId, Authentication auth);
    ReviewDto linkReviewToThesis(Long reviewId, Long thesisId, Authentication auth);
}
