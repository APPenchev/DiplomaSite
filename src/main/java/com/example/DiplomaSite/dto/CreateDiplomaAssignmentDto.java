package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDiplomaAssignmentDto {
    @NotBlank
    private String topic;
    @NotBlank
    private String goal;
    @NotBlank
    private String tasks;
    @NotBlank
    private String technologies;
    @NotBlank
    private Boolean approved;
    @NotBlank
    private Long studentId;
    @NotBlank
    private Long supervisorId;
}
