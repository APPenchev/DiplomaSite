package com.example.DiplomaSite.service.implementation;

import com.example.DiplomaSite.dto.CreateReviewDto;
import com.example.DiplomaSite.dto.ReviewDto;
import com.example.DiplomaSite.dto.UpdateReviewDto;
import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.Review;
import com.example.DiplomaSite.entity.Teacher;
import com.example.DiplomaSite.error.*;
import com.example.DiplomaSite.repository.DiplomaThesisRepository;
import com.example.DiplomaSite.repository.ReviewRepository;
import com.example.DiplomaSite.repository.TeacherRepository;
import com.example.DiplomaSite.service.ReviewService;
import com.example.DiplomaSite.service.validation.ReviewValidator;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class ReviewSeviceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidator reviewValidator;
    private final TeacherRepository teacherRepository;
    private final DiplomaThesisRepository diplomaThesisRepository;

    @Autowired
    public ReviewSeviceImpl(
            ReviewRepository reviewRepository,
            ReviewValidator reviewValidator,
            TeacherRepository teacherRepository,
            DiplomaThesisRepository diplomaThesisRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewValidator = reviewValidator;
        this.teacherRepository = teacherRepository;
        this.diplomaThesisRepository = diplomaThesisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public List<ReviewDto> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth)))")
    public List<ReviewDto> getReviewsByTeacher(
            @Positive @NotNull Long teacherId,
            Authentication auth) {
        return reviewRepository.findByReviewer(teacherId)
                .stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasRole('ROLE_STUDENT') and @reviewSecurity.isAssignee(#id, @keycloakUtils.getUserId(#auth)))")
    public ReviewDto getById(
            @Positive @NotNull Long id,
            Authentication auth
    ) {
        return reviewRepository.findById(id)
                .map(this::toReviewDto)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
    }

    @Override
    @Transactional
    @RolesAllowed({"ROLE_ADMIN", "ROLE_TEACHER"})
    public ReviewDto create(@Valid CreateReviewDto review) {
        reviewValidator.validateText(review.getText());

        Review newReview = new Review();
        newReview.setText(review.getText());
        newReview.setPositive(review.getPositive());
        newReview.setUploadDate(review.getUploadDate());

        Teacher teacher = teacherRepository.findById(review.getTeacherId())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + review.getTeacherId()));
        newReview.setReviewer(teacher);

        try {
            newReview = reviewRepository.save(newReview);
            return toReviewDto(newReview);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewCreationException("Unable to create review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth)))")
    public ReviewDto update(
            @Positive @NotNull Long id,
            UpdateReviewDto review,
            Authentication auth) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        if (review.getText() != null) {
            reviewValidator.validateText(review.getText());
            existingReview.setText(review.getText());
        }

        if (review.getPositive() != null) {
            existingReview.setPositive(review.getPositive());
        }

        if (review.getUploadDate() != null) {
            existingReview.setUploadDate(review.getUploadDate());
        }

        try {
            existingReview = reviewRepository.save(existingReview);
            return toReviewDto(existingReview);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewCreationException("Unable to update review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ROLE_ADMIN"})
    public Long countApprovedReviews() {
        return reviewRepository.countByPositiveFalse();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth)))")
    public void delete(
            @NotNull @Positive Long id,
            Authentication auth) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with id: " + id);
        }

        try {
            reviewRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewDeletionException("Unable to delete review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isAssigner(#reviewId, @keycloakUtils.getUserId(#auth)))")
    public ReviewDto linkReviewToTeacher(
            @NotNull @Positive Long reviewId,
            @NotNull @Positive Long teacherId,
            Authentication auth) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + teacherId));

        if (review.getReviewer() != null) {
            review.getReviewer().getReviews().remove(review);
        }
        review.setReviewer(teacher);

        if (teacher.getReviews().contains(review)) {
            throw new ReviewCreationException("Review is already linked to this teacher", null);
        }
        teacher.getReviews().add(review);

        try {
            review = reviewRepository.save(review);
            teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewCreationException("Unable to link review to teacher due to data integrity issues", e);
        }

        return toReviewDto(review);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "(hasRole('ROLE_TEACHER') and @reviewSecurity.isReviewer(#reviewId, @keycloakUtils.getUserId(#auth)))")
    public ReviewDto linkReviewToThesis(
            @NotNull @Positive Long reviewId,
            @NotNull @Positive Long thesisId,
            Authentication auth) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId)
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + thesisId));


        review.setDiplomaThesis(thesis);
        thesis.setReview(review);

        try {
            review = reviewRepository.save(review);
            diplomaThesisRepository.save(thesis);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewCreationException("Unable to link review to thesis due to data integrity issues", e);
        }

        return toReviewDto(review);
    }


    public ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setText(review.getText());
        reviewDto.setPositive(review.getPositive());
        reviewDto.setUploadDate(review.getUploadDate());
        return reviewDto;
    }
}
