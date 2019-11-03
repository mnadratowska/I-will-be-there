package com.j24.security.template.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class BasicConfig {

    @Bean(name = "myName")
    public String myNameBean() {
        String randomString = UUID.randomUUID().toString();

        return "ONE - " + randomString.substring(0, 5);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
