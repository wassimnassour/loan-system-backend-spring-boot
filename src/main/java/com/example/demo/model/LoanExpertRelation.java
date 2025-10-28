package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class LoanExpertRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_expert_relation_seq")
    @SequenceGenerator(name = "loan_expert_relation_seq", sequenceName = "loan_expert_relation_id_seq", allocationSize = 1)
    private  Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    @JsonBackReference
    private Loan loan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expert_credit_id")
    @JsonBackReference
    private ExpertCredit expertCredit;

}
