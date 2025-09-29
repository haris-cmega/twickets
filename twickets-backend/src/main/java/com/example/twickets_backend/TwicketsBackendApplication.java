package com.example.twickets_backend;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"auth", "config", "demo", "repository", "service", "com.example.twickets_backend"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "entity")
public class TwicketsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwicketsBackendApplication.class, args);
	}

}
