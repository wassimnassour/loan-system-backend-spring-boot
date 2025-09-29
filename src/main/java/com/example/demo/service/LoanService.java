package com.example.demo.service;

import com.example.demo.dto.request.CreateLoanRequestDTO;
import com.example.demo.dto.response.LoanResponseDTO;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.LoanRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LoanService {

    private final LoanRepo loanRepo;

    @Transactional
    public LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Create new loan
        Loan loan = Loan.builder()
                .amount(createLoanRequestDTO.getAmount())
                .interestRate(createLoanRequestDTO.getInterestRate())
                .termInMonths(createLoanRequestDTO.getTermInMonths())
                .purpose(createLoanRequestDTO.getPurpose())
                .notes(createLoanRequestDTO.getNotes())
                .user(currentUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        Loan savedLoan = loanRepo.save(loan);
        return mapToResponseDTO(savedLoan);
    }

    public List<LoanResponseDTO> getAllLoansForAdmin() {
        List<Loan> loans = loanRepo.findAllOrderByApplicationDateDesc();
        return loans.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDTO> getAllLoansForCurrentUser() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<Loan> loans = loanRepo.findByUserOrderByApplicationDateDesc(currentUser);
        return loans.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteLoan(Long loanId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        // Check if the current user is the owner of the loan or is an admin
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !loan.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own loans");
        }

        // Only allow deletion of pending loans
        if (loan.getStatus() != Loan.LoanStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending loans can be deleted");
        }

        loanRepo.delete(loan);
        return "Loan deleted successfully";
    }

    public LoanResponseDTO getLoanById(Long loanId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        // Check if the current user is the owner of the loan or is an admin
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !loan.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own loans");
        }

        return mapToResponseDTO(loan);
    }

    private LoanResponseDTO mapToResponseDTO(Loan loan) {
        return LoanResponseDTO.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .interestRate(loan.getInterestRate())
                .termInMonths(loan.getTermInMonths())
                .purpose(loan.getPurpose())
                .status(loan.getStatus())
                .applicationDate(loan.getApplicationDate())
                .approvalDate(loan.getApprovalDate())
                .disbursementDate(loan.getDisbursementDate())
                .userEmail(loan.getUser().getEmail())
                .userName(loan.getUser().getName())
                .notes(loan.getNotes())
                .build();
    }
}
