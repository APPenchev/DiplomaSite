package com.example.DiplomaSite.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class DiplomaDefense extends IdGenerator {

    private LocalDate date;

    @ManyToMany
    @JoinTable(name = "defense_teacher",
            joinColumns = @JoinColumn(name = "defense_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "diplomaDefense")
    private List<DefenseResult> defenseResults;
}