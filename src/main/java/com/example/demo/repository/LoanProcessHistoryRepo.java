package com.example.demo.repository;

import com.example.demo.model.Loan;
import com.example.demo.model.LoanProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanProcessHistoryRepo extends JpaRepository<LoanProcessHistory , Long> {
    @Query("SELECT l FROM LoanProcessHistory l where l.loan.id =:loanId")
    List<LoanProcessHistory> findLoanProcessHistoriesByLoanId(@Param("loanId") Long loanId);

    Long loan(Loan loan);
}
