package com.sas.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableWebSecurity
@PropertySource("classpath:application-redis-${spring.profiles.active}.properties")
public class SsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
        log.info("SSO-Application started successfully...");
    }
}