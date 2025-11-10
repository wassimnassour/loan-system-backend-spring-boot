package com.example.demo.rabbit.publisher;

import com.example.demo.config.RabbitMQProperties;
import com.example.demo.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

  
    public void publishLoanCreation(Loan loan) {
        try {
            String routingKey = getRoutingKeyForLoanType(loan.getType());
            String exchange = rabbitMQProperties.getExchanges().getLoanDirectExchange();
            
            log.info("Publishing loan creation event - Loan ID: {}, Type: {}, Routing Key: {}", 
                    loan.getId(), loan.getType(), routingKey);
            
            rabbitTemplate.convertAndSend(exchange, routingKey, loan);
            
            log.info("Successfully published loan creation event for Loan ID: {}", loan.getId());
        } catch (Exception e) {
            log.error("Failed to publish loan creation event for Loan ID: {}", loan.getId(), e);
        }
    }

 
    public void publishLoanStatusChange(Long loanId, Loan.LoanStatus oldStatus, Loan.LoanStatus newStatus) {
        try {
            String exchange = rabbitMQProperties.getExchanges().getLoanStatusTopicExchange();
            String routingKey = rabbitMQProperties.getRoutingKeys().getLoanStatusChange();
            
            LoanStatusChangeEvent event = new LoanStatusChangeEvent(loanId, oldStatus, newStatus);
            
            log.info("Publishing loan status change - Loan ID: {}, Old Status: {}, New Status: {}", 
                    loanId, oldStatus, newStatus);
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            
            log.info("Successfully published loan status change event for Loan ID: {}", loanId);
        } catch (Exception e) {
            log.error("Failed to publish loan status change event for Loan ID: {}", loanId, e);
        }
    }


    public void publishLoanNotification(LoanNotificationEvent notification) {
        try {
            String exchange = rabbitMQProperties.getExchanges().getLoanNotificationAlertFanoutExchange();
            
            log.info("Publishing loan notification to fanout exchange - Loan ID: {}", notification.loanId());
            
            rabbitTemplate.convertAndSend(exchange, "", notification);
            
            log.info("Successfully published loan notification for Loan ID: {}", notification.loanId());
        } catch (Exception e) {
            log.error("Failed to publish loan notification for Loan ID: {}", notification.loanId(), e);
        }
    }

 
    private String getRoutingKeyForLoanType(Loan.LoanType type) {
        return switch (type) {
            case PERSONAL -> rabbitMQProperties.getRoutingKeys().getCreateLoanPersonal();
            case AUTO -> rabbitMQProperties.getRoutingKeys().getCreateLoanCar();
            case MORTGAGE -> rabbitMQProperties.getRoutingKeys().getCreateLoanHome();
            default -> rabbitMQProperties.getRoutingKeys().getCreateLoanPersonal(); // fallback
        };
    }

    // Event DTOs
    public record LoanStatusChangeEvent(Long loanId, Loan.LoanStatus oldStatus, Loan.LoanStatus newStatus) {}
    
    public record LoanNotificationEvent(Long loanId, String userEmail, String userName, 
                                       Loan.LoanStatus status, String message) {}
}
