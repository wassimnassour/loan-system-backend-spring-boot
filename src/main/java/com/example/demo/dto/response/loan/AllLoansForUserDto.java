package com.example.demo.dto.response.loan;

import lombok.Data;

import java.util.List;

@Data
public class AllLoansForUserDto {
    private Integer count;
    private List<LoanResponseDTO> loans;
}
