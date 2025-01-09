package com.example.DiplomaSite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
public class DefenseResult extends IdGenerator {

    private Double grade;

    @OneToOne
    @JoinColumn(name = "defense_id", nullable = false)
    private DiplomaDefense diplomaDefense;

    @ManyToOne
    @JoinColumn(name = "thesis_id", nullable = false)
    private DiplomaThesis diplomaThesis;
}