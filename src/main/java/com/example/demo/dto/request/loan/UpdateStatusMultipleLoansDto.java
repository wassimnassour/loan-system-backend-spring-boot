package com.example.demo.dto.request.loan;

import com.example.demo.model.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateStatusMultipleLoansDto {
    @NotNull( message = "status is required")
    private Loan.LoanStatus status;

    @NotNull(message = "List loans are required")
    private List<Long> loanIds;
}
