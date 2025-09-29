package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanRequestDTO {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Loan amount must be at least 1000")
    private BigDecimal amount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1%")
    private BigDecimal interestRate;

    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer termInMonths;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private String notes;
}
