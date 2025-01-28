package com.example.DiplomaSite.controller;

import com.example.DiplomaSite.dto.CreateReviewDto;
import com.example.DiplomaSite.dto.ReviewDto;
import com.example.DiplomaSite.dto.UpdateReviewDto;
import com.example.DiplomaSite.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Retrieves all existing Reviews.
     */
    @Operation(summary = "Get all Reviews",
            description = "Returns a list of all Review records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping
    public ResponseEntity<List<ReviewDto>> findAll() {
        List<ReviewDto> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Retrieves a single Review by its ID.
     */
    @Operation(summary = "Get Review by ID",
            description = "Returns the Review with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved record.")
    @ApiResponse(responseCode = "404", description = "Review not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher','student')")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getById(
            @PathVariable @Positive Long id,
            Authentication auth) {
        ReviewDto review = reviewService.getById(id, auth);
        return ResponseEntity.ok(review);
    }

    /**
     * Retrieves the number of approved Reviews.
     */
    @Operation(summary = "Get Approved Reviews Count",
            description = "Returns the number of approved Review records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/approved-count")
    public ResponseEntity<Long> countApprovedReviews() {
        Long count = reviewService.countApprovedReviews();
        return ResponseEntity.ok(count);
    }


    /**
     * Retrieves all Reviews linked to a Teacher.
     */
    @Operation(summary = "Get Reviews by Teacher",
            description = "Returns a list of all Review records linked to a Teacher.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list.")
    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByTeacher(
            @PathVariable @Positive Long teacherId,
            Authentication auth) {
        List<ReviewDto> reviews = reviewService.getReviewsByTeacher(teacherId, auth);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Creates a new Review.
     */
    @Operation(summary = "Create Review",
            description = "Creates a new Review record.")
    @ApiResponse(responseCode = "201", description = "Review created.")
    @ApiResponse(responseCode = "400", description = "Invalid data provided.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @PostMapping
    public ResponseEntity<ReviewDto> create(
            @RequestBody CreateReviewDto reviewDto) {
        ReviewDto review = reviewService.create(reviewDto);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    /**
     * Updates an existing Review.
     */
    @Operation(summary = "Update Review",
            description = "Updates an existing Review record.")
    @ApiResponse(responseCode = "200", description = "Successfully updated record.")
    @ApiResponse(responseCode = "404", description = "Review not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> update(
            @PathVariable @Positive Long id,
            @RequestBody UpdateReviewDto reviewDto,
            Authentication auth) {
        ReviewDto review = reviewService.update(id, reviewDto, auth);
        return ResponseEntity.ok(review);
    }

    /**
     * Deletes an existing Review.
     */
    @Operation(summary = "Delete Review",
            description = "Deletes an existing Review record.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted record.")
    @ApiResponse(responseCode = "404", description = "Review not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive Long id,
            Authentication auth) {
        reviewService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }

    /**
     * Links Review to Teacher.
     */
    @Operation(summary = "Link Review to Teacher",
            description = "Links a Review to a Teacher.")
    @ApiResponse(responseCode = "200", description = "Successfully linked.")
    @ApiResponse(responseCode = "404", description = "Review or Teacher not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @PostMapping("/{reviewId}/link/{teacherId}")
    public ResponseEntity<ReviewDto> linkTeacher(
            @PathVariable @Positive Long reviewId,
            @PathVariable @Positive Long teacherId,
            Authentication auth) {
        ReviewDto linkedReview = reviewService.linkReviewToTeacher(reviewId, teacherId, auth);
        return ResponseEntity.ok(linkedReview);
    }

    /**
     * Links Review to Thesis.
     */
    @Operation(summary = "Link Review to Thesis",
            description = "Links a Review to a Thesis.")
    @ApiResponse(responseCode = "200", description = "Successfully linked.")
    @ApiResponse(responseCode = "404", description = "Review or Thesis not found.")
    @PreAuthorize("hasAnyAuthority('admin','teacher')")
    @PostMapping("/{reviewId}/link/{thesisId}")
    public ResponseEntity<ReviewDto> linkThesis(
            @PathVariable @Positive Long reviewId,
            @PathVariable @Positive Long thesisId,
            Authentication auth) {
        ReviewDto linkedReview = reviewService.linkReviewToThesis(reviewId, thesisId, auth);
        return ResponseEntity.ok(linkedReview);
    }

}
