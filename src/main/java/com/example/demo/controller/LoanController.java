package com.example.demo.controller;

import com.example.demo.dto.request.CreateLoanRequestDTO;
import com.example.demo.dto.response.LoanResponseDTO;
import com.example.demo.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        LoanResponseDTO loan = loanService.createLoan(createLoanRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoansForAdmin() {
        List<LoanResponseDTO> loans = loanService.getAllLoansForAdmin();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/my-loans")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getMyLoans() {
        List<LoanResponseDTO> loans = loanService.getAllLoansForCurrentUser();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable Long loanId) {
        LoanResponseDTO loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(loan);
    }

    @DeleteMapping("/{loanId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteLoan(@PathVariable Long loanId) {
        String result = loanService.deleteLoan(loanId);
        return ResponseEntity.ok(result);
    }
}
