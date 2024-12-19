package com.example.DiplomaSite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DefenseResult {

    private Double grade;

    @ManyToOne
    @JoinColumn(name = "defense_id", nullable = false)
    private DiplomaDefense diplomaDefense;

    @ManyToOne
    @JoinColumn(name = "thesis_id", nullable = false)
    private DiplomaThesis diplomaThesis;
}