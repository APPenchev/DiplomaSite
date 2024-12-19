package com.example.DiplomaSite.entity;
import com.example.DiplomaSite.enums.TeacherPosition;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Teacher extends IdGenerator {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeacherPosition position;

    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @OneToMany(mappedBy = "supervisor")
    private List<DiplomaAssignment> supervisedAssignments;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviews;

    @ManyToMany(mappedBy = "teachers")
    private List<DiplomaDefense> defenses;

}