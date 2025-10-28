package com.example.demo.repository;


import com.example.demo.model.ExpertCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertCreditRepo extends JpaRepository<ExpertCredit, Long> {
}
