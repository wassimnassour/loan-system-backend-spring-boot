package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {
    private Exchanges exchanges = new Exchanges();
    private Queues queues = new Queues();
    private RoutingKeys routingKeys = new RoutingKeys();

    @Data
    public  static class Exchanges{
        private String loanDirectExchange;
        private  String loanStatusTopicExchange;
        private String loanNotificationAlertFanoutExchange ;
    }

    @Data
    public static class Queues{
        private String personalLoanQueue;
        private String carLoanQueue;
        private String homeLoanQueue;
        private String loanStatusQueue;
        private String notificationSmsQueue;
        private String emailNotificationQueue;
    }


    @Data
    public static class RoutingKeys{
        private String sendLoanWithEmail;
        private String createLoanPersonal;
        private String createLoanCar;
        private String createLoanHome;
        private String loanStatusChange;
    }
}
