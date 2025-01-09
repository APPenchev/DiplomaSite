package com.example.DiplomaSite.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class DiplomaThesis extends IdGenerator {

    @Column(nullable = false)
    private String title;

    @Lob
    private String text;

    private LocalDate uploadDate;

    private Boolean confidential;

    @OneToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private DiplomaAssignment diplomaAssignment;

    @OneToOne(mappedBy = "diplomaThesis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @OneToMany(mappedBy = "diplomaThesis")
    private List<DefenseResult> defenseResults;

}