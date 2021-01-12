package com.example.demotransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoTransactionApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DemoTransactionApplication.class, args);

	}

	

}
