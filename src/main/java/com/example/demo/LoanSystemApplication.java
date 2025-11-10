package com.example.demo;

import com.example.demo.config.RabbitMQProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoanSystemApplication {
	@Autowired
    static RabbitTemplate rabbitTemplate;
	@Autowired
    static RabbitMQProperties rabbitMQProperties;

	public static void main(String[] args) {
		SpringApplication.run(LoanSystemApplication.class, args);
	}

}
