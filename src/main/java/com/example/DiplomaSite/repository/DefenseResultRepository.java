package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DefenseResult;

import java.util.List;

public interface DefenseResultRepository {

    List<DefenseResult> findByGradeBetween(Double minGrade, Double maxGrade);
}
