package com.example.demotransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class DemoTransactionApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DemoTransactionApplication.class, args);

	}
}
