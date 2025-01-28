package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDto extends IdGenerator {

    private String text;
    private LocalDate uploadDate;
    private Boolean positive;
    private String teacherKeycloakId;
    private String studentKeycloakId;
    private Long thesisId;
}
