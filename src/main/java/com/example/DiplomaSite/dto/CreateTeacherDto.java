package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeacherDto {
    @NotBlank
    private String name;
    @NotBlank
    private TeacherPosition position;
}
