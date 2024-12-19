package com.example.DiplomaSite.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Student extends IdGenerator {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String facultyNumber;

    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private DiplomaAssignment diplomaAssignment;

}