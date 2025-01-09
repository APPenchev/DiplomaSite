package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.IdGenerator;
import lombok.Data;

@Data
public class StudentDto extends IdGenerator {

    private String name;
    private String facultyNumber;
    private String keycloakUserId;
    private Long diplomaAssignmentId;
}
