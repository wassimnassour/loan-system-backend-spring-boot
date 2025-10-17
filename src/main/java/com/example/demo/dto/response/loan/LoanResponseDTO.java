package com.example.demo.dto.response.loan;

import com.example.demo.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponseDTO {
    private Long id;
    private BigDecimal amount;
    private Loan.LoanType type;
    private BigDecimal interestRate;
    private Integer termInMonths;
    private String purpose;
    private Loan.LoanStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private LocalDateTime disbursementDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;
    private String userName;
    private String notes;
}

