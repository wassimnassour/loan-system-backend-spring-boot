package com.example.demo.rabbit.consumer;

import com.example.demo.model.Loan;
import com.example.demo.rabbit.publisher.LoanPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanConsumer {

    /**
     * Listen for personal loan creation events
     */
    @RabbitListener(queues = "${rabbitmq.queues.personalLoanQueue}")
    public void handlePersonalLoanCreation(Loan loan) {
        try {
            log.info("Received personal loan creation event - Loan ID: {}, Amount: {}", 
                    loan.getId(), loan.getAmount());
            
            // Process personal loan (e.g., run credit checks, assign to specialist)
            processLoan(loan, "PERSONAL");
            
            log.info("Successfully processed personal loan - Loan ID: {}", loan.getId());
        } catch (Exception e) {
            log.error("Error processing personal loan - Loan ID: {}", loan.getId(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }

    /**
     * Listen for car loan creation events
     */
    @RabbitListener(queues = "${rabbitmq.queues.carLoanQueue}")
    public void handleCarLoanCreation(Loan loan) {
        try {
            log.info("Received car loan creation event - Loan ID: {}, Amount: {}", 
                    loan.getId(), loan.getAmount());
            
            // Process car loan (e.g., verify vehicle details, check collateral)
            processLoan(loan, "CAR");
            
            log.info("Successfully processed car loan - Loan ID: {}", loan.getId());
        } catch (Exception e) {
            log.error("Error processing car loan - Loan ID: {}", loan.getId(), e);
            throw e;
        }
    }

    /**
     * Listen for home loan creation events
     */
    @RabbitListener(queues = "${rabbitmq.queues.homeLoanQueue}")
    public void handleHomeLoanCreation(Loan loan) {
        try {
            log.info("Received home loan creation event - Loan ID: {}, Amount: {}", 
                    loan.getId(), loan.getAmount());
            
            // Process home loan (e.g., property valuation, mortgage verification)
            processLoan(loan, "HOME");
            
            log.info("Successfully processed home loan - Loan ID: {}", loan.getId());
        } catch (Exception e) {
            log.error("Error processing home loan - Loan ID: {}", loan.getId(), e);
            throw e;
        }
    }

    /**
     * Listen for loan status change events
     */
    @RabbitListener(queues = "${rabbitmq.queues.loanStatusQueue}")
    public void handleLoanStatusChange(LoanPublisher.LoanStatusChangeEvent event) {
        try {
            log.info("Received loan status change event - Loan ID: {}, Old: {}, New: {}", 
                    event.loanId(), event.oldStatus(), event.newStatus());
            
            // Process status change (e.g., update analytics, trigger workflows)
            processStatusChange(event);
            
            log.info("Successfully processed loan status change - Loan ID: {}", event.loanId());
        } catch (Exception e) {
            log.error("Error processing loan status change - Loan ID: {}", event.loanId(), e);
            throw e;
        }
    }

    /**
     * Listen for email notification events
     */
    @RabbitListener(queues = "${rabbitmq.queues.emailNotificationQueue}")
    public void handleEmailNotification(LoanPublisher.LoanNotificationEvent notification) {
        try {
            log.info("Received email notification event - Loan ID: {}, Email: {}", 
                    notification.loanId(), notification.userEmail());
            
            // Send email notification
            sendEmail(notification);
            
            log.info("Successfully sent email notification - Loan ID: {}", notification.loanId());
        } catch (Exception e) {
            log.error("Error sending email notification - Loan ID: {}", notification.loanId(), e);
            throw e;
        }
    }

    /**
     * Listen for SMS notification events
     */
    @RabbitListener(queues = "${rabbitmq.queues.notificationSmsQueue}")
    public void handleSmsNotification(LoanPublisher.LoanNotificationEvent notification) {
        try {
            log.info("Received SMS notification event - Loan ID: {}, User: {}", 
                    notification.loanId(), notification.userName());
            
            // Send SMS notification
            sendSms(notification);
            
            log.info("Successfully sent SMS notification - Loan ID: {}", notification.loanId());
        } catch (Exception e) {
            log.error("Error sending SMS notification - Loan ID: {}", notification.loanId(), e);
            throw e;
        }
    }

    // Helper methods
    
    private void processLoan(Loan loan, String loanType) {
        // TODO: Implement loan processing logic
        // - Credit score verification
        // - Document validation
        // - Risk assessment
        // - Assignment to loan officer
        log.info("Processing {} loan with ID: {}", loanType, loan.getId());
    }

    private void processStatusChange(LoanPublisher.LoanStatusChangeEvent event) {
        // TODO: Implement status change processing
        // - Update dashboards
        // - Trigger notifications
        // - Update reporting systems
        log.info("Processing status change from {} to {} for Loan ID: {}", 
                event.oldStatus(), event.newStatus(), event.loanId());
    }

    private void sendEmail(LoanPublisher.LoanNotificationEvent notification) {
        // TODO: Implement email sending logic
        // - Format email template
        // - Send via email service (e.g., SendGrid, AWS SES)
        log.info("Sending email to {} for Loan ID: {}", 
                notification.userEmail(), notification.loanId());
    }

    private void sendSms(LoanPublisher.LoanNotificationEvent notification) {
        // TODO: Implement SMS sending logic
        // - Format SMS message
        // - Send via SMS service (e.g., Twilio, AWS SNS)
        log.info("Sending SMS to user {} for Loan ID: {}", 
                notification.userName(), notification.loanId());
    }
}
