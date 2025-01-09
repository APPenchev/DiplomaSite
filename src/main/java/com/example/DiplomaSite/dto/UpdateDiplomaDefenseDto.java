package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDiplomaDefenseDto extends IdGenerator {

    private LocalDate date;

    private Long supervisorId;
}
