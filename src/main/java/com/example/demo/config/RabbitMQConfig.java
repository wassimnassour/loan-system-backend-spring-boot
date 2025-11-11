package com.example.demo.config;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    final private RabbitMQProperties rabbitMQProperties;



    @Bean
    public Queue personalLoanQueue(){
        return new Queue(rabbitMQProperties.getQueues().getPersonalLoanQueue(), true, false, false);
    }

    @Bean
    public Queue homeLoanQueue(){
        return new Queue(rabbitMQProperties.getQueues().getHomeLoanQueue(), true, false, false);
    }

    @Bean
    public Queue carLoanQueue(){
        return new Queue(rabbitMQProperties.getQueues().getCarLoanQueue(), true, false, false);
    }

    @Bean
    public Queue loanStatusQueue(){
        return new Queue(rabbitMQProperties.getQueues().getLoanStatusQueue(), true, false, false);
    }

    @Bean
    public Queue notificationSmsQueue(){
        return new Queue(rabbitMQProperties.getQueues().getNotificationSmsQueue(), true, false, false);
    }
    @Bean
    public Queue notificationEmailQueue(){
        return new Queue(rabbitMQProperties.getQueues().getEmailNotificationQueue(), true, false, false);
    }





    @Bean
    public DirectExchange directExchangeLoanCreation(){
        return new DirectExchange(rabbitMQProperties.getExchanges().getLoanDirectExchange());
    }


    @Bean
    public FanoutExchange fanoutAlertLoanNotification(){
        return new FanoutExchange(rabbitMQProperties.getExchanges().getLoanNotificationAlertFanoutExchange());
    }

    @Bean
    public TopicExchange topicExchangeLoanStatus(){
        return new TopicExchange(rabbitMQProperties.getExchanges().getLoanStatusTopicExchange());
    }




    //    bind 3 queues to one direct exchange
    @Bean
    public Binding bindCreatePersonalLoanQueueToDirectExchange(){
        return BindingBuilder.bind(personalLoanQueue()).to(directExchangeLoanCreation()).with(rabbitMQProperties.getRoutingKeys().getCreateLoanPersonal());
    }

    @Bean
    public Binding bindCreateLoanHomeQueueToDirectExchange(){
        return BindingBuilder.bind(homeLoanQueue()).to(directExchangeLoanCreation()).with(rabbitMQProperties.getRoutingKeys().getCreateLoanHome());
    }
    @Bean
    public Binding bindCreateLoanCarQueueToDirectExchange(){
        return BindingBuilder.bind(carLoanQueue()).to(directExchangeLoanCreation()).with(rabbitMQProperties.getRoutingKeys().getCreateLoanCar());
    }


//    topic exchange
    @Bean
    public Binding bindLoanStatusToTopicExchange(){
        return BindingBuilder.bind(loanStatusQueue()).to(topicExchangeLoanStatus()).with(rabbitMQProperties.getRoutingKeys().getLoanStatusChange());
    }


//    fanout exchange for notification with email and phone sms

    @Bean
    public Binding bindLoanSmsNotificationToFanoutExchange(){
        return BindingBuilder.bind(notificationSmsQueue()).to(fanoutAlertLoanNotification());
    }

    @Bean
    public Binding bindLoanEmailNotificationToFanoutExchange(){
        return BindingBuilder.bind(notificationEmailQueue()).to(fanoutAlertLoanNotification());
    }

}
