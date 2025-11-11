package com.example.demo.service;

import com.example.demo.dto.request.CreateLoanRequestDTO;
import com.example.demo.dto.request.loan.LoanFilter;
import com.example.demo.dto.request.loan.UpdateLoanStatusCreateDTO;
import com.example.demo.dto.request.loan.UpdateStatusMultipleLoansDto;
import com.example.demo.dto.response.loan.LoanResponseDTO;
import com.example.demo.model.Document;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanProcessHistory;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import com.example.demo.specification.Loan.LoanSpecification;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LoanService {

    private final LoanRepo loanRepo;
    private final DocumentRepo documentRepo;
    private  final LoanProcessHistoryRepo loanProcessHistoryRepo;

    private final LoanExpertRepoRelation loanExpertRepoRelation;
    private  final ExpertCreditRepo expertCreditRepo;
    
    private final com.example.demo.rabbit.publisher.LoanPublisher loanPublisher;


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
        
        // Publish loan creation event to RabbitMQ
        loanPublisher.publishLoanCreation(savedLoan);
        
        return mapToResponseDTO(savedLoan);
    }

    public Page<LoanResponseDTO> getAllLoansByRoleAndFilters(LoanFilter loanFilter , Pageable pageable, User user) {
        Specification<Loan> loanSpecificationWithFilters =  LoanSpecification.withFilters(loanFilter , user);
        Page<Loan> loans = loanRepo.findAll(loanSpecificationWithFilters , pageable);
        return loans.map(this::mapToResponseDTO);
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
                .experts(loan.getAssignedTos())
                .build();
    }

    @Transactional
    public void updateLoanStatus(Long loanId ,  UpdateLoanStatusCreateDTO updateLoanStatus) throws BadRequestException {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Loan loan = loanRepo.findLoanById(loanId);
            
            Loan.LoanStatus oldStatus = loan.getStatus();

            LoanProcessHistory loanProcessHistory = new LoanProcessHistory();
            loanProcessHistory.setToStatus(updateLoanStatus.getToStatus());
            loanProcessHistory.setComment(updateLoanStatus.getComment());
            loanProcessHistory.setLoan(loan);
            loanProcessHistory.setFromStatus(oldStatus);
            loanProcessHistory.setPerformedBy(((User) authentication.getPrincipal()).getId());
            loan.setStatus(updateLoanStatus.getToStatus());
            loanRepo.save(loan);
            loanProcessHistoryRepo.save(loanProcessHistory);
            
            // Publish status change event to RabbitMQ
            loanPublisher.publishLoanStatusChange(loanId, oldStatus, updateLoanStatus.getToStatus());
            
            // Publish notification to fanout exchange (email + SMS)
            com.example.demo.rabbit.publisher.LoanPublisher.LoanNotificationEvent notification = 
                new com.example.demo.rabbit.publisher.LoanPublisher.LoanNotificationEvent(
                    loan.getId(),
                    loan.getUser().getEmail(),
                    loan.getUser().getName(),
                    updateLoanStatus.getToStatus(),
                    "Your loan status has been updated to: " + updateLoanStatus.getToStatus()
                );
            loanPublisher.publishLoanNotification(notification);
            
        }catch (Exception e){
            throw new BadRequestException(e);
        }
    }
    public List<LoanProcessHistory> listLoanProcessHistory(Long loanId) {
        try {
            return loanProcessHistoryRepo.findLoanProcessHistoriesByLoanId(loanId);

        }catch (Exception e){
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found");
        }
    }

    @Transactional
    public void assignExpertToLoan(Long loanId, Long expertId) throws BadRequestException {
        try{
            boolean loanExists = loanRepo.existsById(loanId);
            if (!loanExists) {
                throw new BadRequestException("Loan is not exists");
            }

            boolean expertExists = expertCreditRepo.existsById(expertId);
            if (!expertExists) {
                throw new BadRequestException("Expert is not exists");
            }

            loanExpertRepoRelation.assignExpertToLoan(expertId, loanId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void updateStatusOfMultipleLoans(UpdateStatusMultipleLoansDto updateStatusMultipleLoansDto){
        // Get all loans before update to capture old status
        List<Loan> loans = loanRepo.findAllById(updateStatusMultipleLoansDto.getLoanIds());
        
        // Perform bulk update
        loanRepo.bulkUpdateStatusOfLoans(updateStatusMultipleLoansDto.getStatus(), updateStatusMultipleLoansDto.getLoanIds());
        
        // Publish status change events for each loan
        for (Loan loan : loans) {
            Loan.LoanStatus oldStatus = loan.getStatus();
            
            // Publish status change event
            loanPublisher.publishLoanStatusChange(
                loan.getId(), 
                oldStatus, 
                updateStatusMultipleLoansDto.getStatus()
            );
            
            // Publish notification
            com.example.demo.rabbit.publisher.LoanPublisher.LoanNotificationEvent notification = 
                new com.example.demo.rabbit.publisher.LoanPublisher.LoanNotificationEvent(
                    loan.getId(),
                    loan.getUser().getEmail(),
                    loan.getUser().getName(),
                    updateStatusMultipleLoansDto.getStatus(),
                    "Your loan status has been updated to: " + updateStatusMultipleLoansDto.getStatus()
                );
            loanPublisher.publishLoanNotification(notification);
        }
    }

}
