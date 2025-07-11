package org.rmc.training_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TrainingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainingPlatformApplication.class, args);
	}

}
