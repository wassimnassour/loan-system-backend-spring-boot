package com.example.demo;

import org.springframework.boot.SpringApplication;

public class TestLoanSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(LoanSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
