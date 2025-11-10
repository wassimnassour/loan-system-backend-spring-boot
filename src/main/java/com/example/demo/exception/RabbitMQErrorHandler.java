package com.example.demo.exception;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

@Component("rabbitMQErrorHandler")
@Slf4j
public class RabbitMQErrorHandler implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(Message amqpMessage, Channel channel, org.springframework.messaging.Message<?> message, 
                            ListenerExecutionFailedException exception) {
        log.error("RabbitMQ message processing failed. Queue: {}, Message: {}, Error: {}", 
                amqpMessage.getMessageProperties().getConsumerQueue(),
                new String(amqpMessage.getBody()),
                exception.getMessage(), exception);

        // Determine if the error is recoverable
        if (isRecoverableError(exception)) {
            log.warn("Recoverable error detected. Message will be requeued.");
            // Let Spring AMQP handle retry
            throw exception;
        } else {
            log.error("Non-recoverable error detected. Message will be rejected and sent to DLQ if configured.");
            // Reject and don't requeue (will go to DLQ if configured)
            throw new AmqpRejectAndDontRequeueException("Non-recoverable error processing message", exception);
        }
    }

    /**
     * Determine if an error is recoverable (e.g., temporary network issues, database connection problems)
     * vs non-recoverable (e.g., validation errors, malformed data)
     */
    private boolean isRecoverableError(Exception exception) {
        // Check for common recoverable exceptions
        String errorMessage = exception.getMessage().toLowerCase();
        
        // Recoverable errors - database, network, timeout issues
        if (errorMessage.contains("timeout") || 
            errorMessage.contains("connection") || 
            errorMessage.contains("database") ||
            errorMessage.contains("network")) {
            return true;
        }
        
        // Non-recoverable errors - validation, parsing, business logic
        if (errorMessage.contains("validation") || 
            errorMessage.contains("illegal") || 
            errorMessage.contains("parse") ||
            errorMessage.contains("null pointer")) {
            return false;
        }
        
        // Default to recoverable for safety (will retry)
        return true;
    }
}
