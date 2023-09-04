package com.coutinho.userservicemailsender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${spring.mail.username}")
    private String mailName;
}
