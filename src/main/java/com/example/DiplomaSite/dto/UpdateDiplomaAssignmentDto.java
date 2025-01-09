package com.example.DiplomaSite.dto;

import lombok.Data;

@Data
public class UpdateDiplomaAssignmentDto {
    private String topic;
    private String goal;
    private String tasks;
    private String technologies;
    private Boolean approved;
}
