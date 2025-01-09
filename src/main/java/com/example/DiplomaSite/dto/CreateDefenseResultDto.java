package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDefenseResultDto {

    @NotBlank
    private Double grade;

    @NotBlank
    private Long defenseId;


}
