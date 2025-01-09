package com.example.DiplomaSite.dto;

import lombok.Data;
import com.example.DiplomaSite.entity.IdGenerator;

@Data
public class DefenseResultDto extends IdGenerator{
    private Double grade;
    private Long defenseId;
}
