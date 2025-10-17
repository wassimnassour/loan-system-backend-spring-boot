package com.example.demo.controller;

import com.example.demo.dto.request.CreateLoanRequestDTO;
import com.example.demo.dto.request.loan.LoanFilter;
import com.example.demo.dto.request.loan.UpdateLoanStatusCreateDTO;
import com.example.demo.dto.response.loan.AllLoansForUserDto;
import com.example.demo.dto.response.loan.LoanResponseDTO;
import com.example.demo.model.Document;
import com.example.demo.model.LoanProcessHistory;
import com.example.demo.model.User;
import com.example.demo.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/loans")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private static final String UPLOAD_DIR = "uploads/"; // relative to project root


    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        LoanResponseDTO loan = loanService.createLoan(createLoanRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AllLoansForUserDto> getAllLoansForAdmin(@RequestParam(required = false) String clientName,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                  @RequestParam(required = false) Double minAmount,
                                                                  @RequestParam(required = false) Double maxAmount,
                                                                  Pageable pageable
    ) {
        LoanFilter loanFilter = new LoanFilter();
        loanFilter.setStatus(status);
        loanFilter.setStartDate(startDate);
        loanFilter.setEndDate(endDate);
        loanFilter.setMaxAmount(maxAmount   );
        loanFilter.setMinAmount(minAmount);

         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Page<LoanResponseDTO> loansPage = loanService.getAllLoansByRoleAndFilters(loanFilter, pageable, currentUser);

        AllLoansForUserDto response = new AllLoansForUserDto();
        response.setCount((int) loansPage.getTotalElements());
        response.setLoans(loansPage.getContent());

        return ResponseEntity.ok(response);
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


    @PostMapping("/document/upload")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadDocument1(@RequestParam("file") MultipartFile file) throws IOException {
        loanService.uploadDocument(file);
        return ResponseEntity.ok("File uploaded successfully to " + file.getName());
    }

    @GetMapping("/document/all")
    public ResponseEntity<List<Document>> getAllDocuments(@RequestParam("userId") Long userId) {
         return ResponseEntity.ok(loanService.listDocuments(userId));
    }

    @PostMapping("/status/{id}")
    public void updateLoanStatus(@PathVariable("id") Long loanId,  @RequestBody @Valid  UpdateLoanStatusCreateDTO updateLoanStatus) throws BadRequestException {
        loanService.updateLoanStatus(loanId , updateLoanStatus);
    }

    @GetMapping("/{loanId}/history")
    public ResponseEntity<List<LoanProcessHistory>> getLoanProcessHistory(@PathVariable("loanId") Long loanId) {
        return ResponseEntity.ok(loanService.listLoanProcessHistory(loanId));
    }
}

