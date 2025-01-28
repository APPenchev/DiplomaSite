package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DiplomaDefenseDto extends IdGenerator {

    private LocalDate date;
    private String supervisorKeycloakId;
    private List<String> committeeMembersKeycloakIds;
    private Long resultId;
}
