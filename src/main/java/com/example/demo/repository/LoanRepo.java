package com.example.demo.repository;

import com.example.demo.model.Loan;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepo extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    
    List<Loan> findByUserOrderByApplicationDateDesc(User user);
    
    @Query("SELECT l FROM Loan l ORDER BY l.applicationDate DESC")
    List<Loan> findAllOrderByApplicationDateDesc();
    
    @Query("SELECT l FROM Loan l WHERE l.status = :status ORDER BY l.applicationDate DESC")
    List<Loan> findByStatusOrderByApplicationDateDesc(@Param("status") Loan.LoanStatus status);
    
    long countByUser(User user);
}
