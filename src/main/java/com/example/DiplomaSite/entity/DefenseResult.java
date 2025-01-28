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
    @JoinColumn(name = "defense_id")
    private DiplomaDefense diplomaDefense;


}