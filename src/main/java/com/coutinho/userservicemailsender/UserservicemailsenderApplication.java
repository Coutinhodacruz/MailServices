package com.coutinho.userservicemailsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UserservicemailsenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserservicemailsenderApplication.class, args);
	}

}
