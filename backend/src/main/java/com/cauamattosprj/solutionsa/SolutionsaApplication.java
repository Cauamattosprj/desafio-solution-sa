package com.cauamattosprj.solutionsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SolutionsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolutionsaApplication.class, args);
	}

}
