package com.example.DiplomaSite.service;

import com.example.DiplomaSite.dto.CreateReviewDto;
import com.example.DiplomaSite.dto.ReviewDto;
import com.example.DiplomaSite.dto.UpdateReviewDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {


    List<ReviewDto> findAll();

    List<ReviewDto> getReviewsByTeacher(
            @Positive @NotNull Long teacherId,
            Authentication auth);

    ReviewDto getById(
            @Positive @NotNull Long id,
            Authentication auth
    );

    ReviewDto create(@Valid CreateReviewDto review);

    ReviewDto update(
            @Positive @NotNull Long id,
            UpdateReviewDto review,
            Authentication auth);

    Long countApprovedReviews();

    void delete(
            @NotNull @Positive Long id,
            Authentication auth);

    ReviewDto linkReviewToTeacher(
            @NotNull @Positive Long reviewId,
            @NotNull @Positive Long teacherId,
            Authentication auth);

    ReviewDto linkReviewToThesis(
            @NotNull @Positive Long reviewId,
            @NotNull @Positive Long thesisId,
            Authentication auth);
}
