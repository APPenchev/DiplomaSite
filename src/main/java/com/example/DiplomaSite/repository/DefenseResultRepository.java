package com.example.DiplomaSite.repository;

import com.example.DiplomaSite.entity.DefenseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefenseResultRepository extends JpaRepository<DefenseResult, Long>{


}
