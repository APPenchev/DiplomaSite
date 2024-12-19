package com.example.DiplomaSite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DiplomaAssignment extends IdGenerator {


    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String goal;

    @Lob
    private String tasks;

    @Lob
    private String technologies;

    private Boolean approved;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher supervisor;

    @OneToOne(mappedBy = "diplomaAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private DiplomaThesis diplomaThesis;

}