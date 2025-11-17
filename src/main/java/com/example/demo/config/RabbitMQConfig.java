package com.example.demo.config;


import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    final private RabbitMQProperties rabbitMQProperties;


    public Queue buildLoanQueue(String queueName ){
        Map<String , Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", rabbitMQProperties.getDlx().getLoan());
        args.put("x-dead-letter-routing-key", rabbitMQProperties.getRoutingKeys().getLoanDlqDlx());
        return new Queue(queueName, true, false, false);
    }

    @Bean
    public Queue personalLoanQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getPersonalLoanQueue());
    }

    @Bean
    public Queue homeLoanQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getHomeLoanQueue());
    }

    @Bean
    public Queue carLoanQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getCarLoanQueue());
    }

    @Bean
    public Queue loanStatusQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getLoanStatusQueue());
    }

    @Bean
    public Queue notificationSmsQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getNotificationSmsQueue());
    }
    @Bean
    public Queue notificationEmailQueue(){
        return buildLoanQueue(rabbitMQProperties.getQueues().getEmailNotificationQueue());
    }


    @Bean
    public Queue loanDlq(){
        return buildLoanQueue(rabbitMQProperties.getDlq().getLoan());
    }

//    EXCHANGES

    @Bean
    public DirectExchange loanDlxExchange(){
        return new DirectExchange(rabbitMQProperties.getDlx().getLoan());
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

    @Bean
    public Binding bindLoanDlqToDlxExchange(){
        return BindingBuilder.bind(loanDlq()).to(loanDlxExchange()).with(rabbitMQProperties.getRoutingKeys().getLoanDlqDlx());
    }

}
