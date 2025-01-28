package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDefenseResultDto {

    @NotNull
    private Double grade;

    @NotNull
    private Long defenseId;


}
