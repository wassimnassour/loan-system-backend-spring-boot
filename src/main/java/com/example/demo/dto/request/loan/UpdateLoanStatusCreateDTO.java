package com.example.demo.dto.request.loan;

import com.example.demo.model.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLoanStatusCreateDTO {

    @NotNull
    private  String comment;
    @NotNull
    private Loan.LoanStatus toStatus;
}
