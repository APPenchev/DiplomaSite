package com.example.DiplomaSite.dto;

import com.example.DiplomaSite.entity.DiplomaAssignment;
import com.example.DiplomaSite.entity.DiplomaDefense;
import com.example.DiplomaSite.entity.IdGenerator;
import com.example.DiplomaSite.entity.Review;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class TeacherDto extends IdGenerator {
    private String name;
    private TeacherPosition position;
    private String keycloakUserId;
}