package com.example.demo.dto.request.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanFilter {
    private String clientName;
    private String status;
    private Double minAmount;
    private Double maxAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page;
    private Integer size;

}
