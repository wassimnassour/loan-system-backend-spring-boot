package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class LoanProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    @JsonIgnore
    private Loan loan;

    @Enumerated(EnumType.STRING)
    private Loan.LoanStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private Loan.LoanStatus toStatus;

    private String comment;
    private Long performedBy;

    private LocalDateTime performedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}
