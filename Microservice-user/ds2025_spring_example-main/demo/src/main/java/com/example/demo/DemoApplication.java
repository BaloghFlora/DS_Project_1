package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // <-- ADD THIS
import org.springframework.web.client.RestTemplate; // <-- ADD THIS

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean // <-- ADD THIS METHOD
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}