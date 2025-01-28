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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAuthorities('admin')")
    public List<ReviewDto> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthorities('admin')")
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
    @PreAuthorize("hasAuthority('admin') or " +
            "hasAuthority('teacher') or " +
            "(hasAuthority('student') and @reviewSecurity.isAssignee(#id, @keycloakUtils.getUserId(#auth)))")
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
    @PreAuthorize("hasAnyAuthority('admin', 'teacher')")
    public ReviewDto create(@Valid CreateReviewDto review) {
        reviewValidator.validateText(review.getText());

        Review newReview = new Review();
        newReview.setText(review.getText());
        newReview.setPositive(review.getPositive());
        newReview.setUploadDate(review.getUploadDate());

        Teacher teacher = teacherRepository.findById(review.getTeacherId())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + review.getTeacherId()));
        newReview.setReviewer(teacher);

        DiplomaThesis thesis = diplomaThesisRepository.findById(review.getThesisId())
                .orElseThrow(() -> new DiplomaThesisNotFoundException("Diploma thesis not found with id: " + review.getThesisId()));
        newReview.setDiplomaThesis(thesis);

        try {
            newReview = reviewRepository.save(newReview);
            return toReviewDto(newReview);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewCreationException("Unable to create review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAuthority('teacher') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth)))")
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
    @PreAuthorize("hasAnyAuthority('admin')")
    public Long countApprovedReviews() {
        return reviewRepository.countByPositiveFalse();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAuthority('teacher') and @reviewSecurity.isReviewer(#id, @keycloakUtils.getUserId(#auth))) or " +
            "(hasAuthority('student') and @reviewSecurity.isAssignee(#id, @keycloakUtils.getUserId(#auth)))")
    public void delete(
            @NotNull @Positive Long id,
            Authentication auth) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
        if (review.getDiplomaThesis() != null) {
            review.getDiplomaThesis().setReview(null);
            diplomaThesisRepository.save(review.getDiplomaThesis());
        }

        try {
            reviewRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ReviewDeletionException("Unable to delete review due to data integrity issues", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAuthority('teacher') and !@reviewSecurity.isAssigner(#reviewId, @keycloakUtils.getUserId(#auth)))")
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
    @PreAuthorize("hasAnyAuthority('admin') or " +
            "(hasAuthority('teacher') and @reviewSecurity.isAssigner(#reviewId, @keycloakUtils.getUserId(#auth)))")
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
        if (review.getReviewer() != null) {
            reviewDto.setTeacherKeycloakId(review.getReviewer().getKeycloakUserId());
        }
        if (review.getDiplomaThesis() != null) {
            reviewDto.setThesisId(review.getDiplomaThesis().getId());
            if(review.getDiplomaThesis().getDiplomaAssignment() != null) {
                if (review.getDiplomaThesis().getDiplomaAssignment().getStudent() != null) {
                    reviewDto.setStudentKeycloakId(review.getDiplomaThesis().getDiplomaAssignment().getStudent().getKeycloakUserId());
                }

            }
        }
        return reviewDto;
    }
}
