package com.example.DiplomaSite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentStatusProjection {
    private Long assignmentId;
    private String topic;
    private Boolean approved;
    private Boolean hasThesis;
    private Boolean thesisReviewed;
    private Boolean positiveReview;
    private Boolean hasDefense;
}