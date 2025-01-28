package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.DiplomaThesis;
import com.example.DiplomaSite.entity.IdGenerator;
import com.example.DiplomaSite.entity.Student;
import com.example.DiplomaSite.entity.Teacher;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class DiplomaAssignmentDto extends IdGenerator {

    private String topic;
    private String goal;
    private String tasks;
    private String technologies;
    private Boolean approved;
    private Long studentId;
    private Long supervisorId;
    private Long thesisId;
}