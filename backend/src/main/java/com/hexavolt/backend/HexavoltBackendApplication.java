package com.hexavolt.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.hexavolt.backend.entity")
@EnableJpaRepositories("com.hexavolt.backend.repository")
public class HexavoltBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HexavoltBackendApplication.class, args);
	}
}
