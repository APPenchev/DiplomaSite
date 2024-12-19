package com.example.DiplomaSite.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Review extends IdGenerator{

    @Lob
    private String text;

    private LocalDate uploadDate;

    private Boolean positive;

    @OneToOne
    @JoinColumn(name = "thesis_id", nullable = false)
    private DiplomaThesis diplomaThesis;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher reviewer;

}