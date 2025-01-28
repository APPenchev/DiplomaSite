package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeacherDto {
    @NotBlank
    private String name;
    @NotNull
    private TeacherPosition position;
}
