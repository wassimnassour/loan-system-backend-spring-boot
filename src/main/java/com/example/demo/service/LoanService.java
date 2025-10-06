package com.example.demo.service;

import com.example.demo.dto.request.CreateLoanRequestDTO;
import com.example.demo.dto.response.LoanResponseDTO;
import com.example.demo.model.Document;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.DocumentRepo;
import com.example.demo.repository.LoanRepo;
import com.example.demo.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LoanService {

    private final LoanRepo loanRepo;
    private final DocumentRepo documentRepo;


    @Transactional
    public LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Create new loan
        Loan loan = Loan.builder()
                .amount(createLoanRequestDTO.getAmount())
                .type(createLoanRequestDTO.getType())
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

    @Transactional
    public void uploadDocument(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Path uploadDirectory = Paths.get("uploads");

        Files.createDirectories(uploadDirectory);

        if (file.isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        String originalName = currentUser.getId()+"-"+file.getOriginalFilename();
        Path filePath = uploadDirectory.resolve(originalName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        Document document = new  Document();
        document.setUser(currentUser);
        document.setFilePath(filePath.getFileName().toString());
        document.setFileName(originalName);
        documentRepo.save(document);

    }

    public List<Document> listDocuments(Long userId){
        return documentRepo.findAllByUserId(userId);
    }
    private LoanResponseDTO mapToResponseDTO(Loan loan) {
        return LoanResponseDTO.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .type(loan.getType())
                .interestRate(loan.getInterestRate())
                .termInMonths(loan.getTermInMonths())
                .purpose(loan.getPurpose())
                .status(loan.getStatus())
                .applicationDate(loan.getApplicationDate())
                .approvalDate(loan.getApprovalDate())
                .disbursementDate(loan.getDisbursementDate())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .userEmail(loan.getUser().getEmail())
                .userName(loan.getUser().getName())
                .notes(loan.getNotes())
                .build();
    }

}
