package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTeacherDto {
    private String name;
    private TeacherPosition position;
    private String keycloakUserId;
}
