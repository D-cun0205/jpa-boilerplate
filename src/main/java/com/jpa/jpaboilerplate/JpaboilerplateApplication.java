package com.jpa.jpaboilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JpaboilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaboilerplateApplication.class, args);
	}
}
