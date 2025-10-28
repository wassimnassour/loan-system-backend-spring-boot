package com.example.demo.repository;

import com.example.demo.model.LoanExpertRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanExpertRepoRelation extends JpaRepository<LoanExpertRelation, Long> {

    @Modifying
    @Query(
            value = "INSERT INTO loan_expert_relation (id, loan_id, expert_credit_id) VALUES (nextval('loan_expert_relation_id_seq'), :loanId, :expertId)",
            nativeQuery = true
    )
    void assignExpertToLoan(@Param("expertId")  Long expertId,@Param("loanId") Long loanId );

}
