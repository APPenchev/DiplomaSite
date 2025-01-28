package com.example.DiplomaSite.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.DiplomaSite.repository.ReviewRepository;

@Component
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewSecurity(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public boolean isReviewer(Long reviewId, String keycloakUserId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getReviewer().getKeycloakUserId().equals(keycloakUserId)).orElse(false);

    }

    public boolean isAssigner(Long reviewId, String keycloakUserId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getDiplomaThesis().getDiplomaAssignment().getSupervisor().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }

    public boolean isAssignee(Long reviewId, String keycloakUserId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getDiplomaThesis().getDiplomaAssignment().getStudent().getKeycloakUserId().equals(keycloakUserId)).orElse(false);
    }


}
