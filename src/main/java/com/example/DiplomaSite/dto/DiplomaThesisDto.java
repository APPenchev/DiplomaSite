package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiplomaThesisDto extends IdGenerator {
    private String title;
    private String text;
    private LocalDate uploadDate;
    private Boolean confidential;
    private Long assignmentId;
    private String studentKeycloakId;
    private String teacherKeycloakId;
    private Long reviewId;
}
