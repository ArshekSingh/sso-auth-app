package com.sas.sso;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ComponentScan("com.sas.sso")
@EntityScan("com.sas.sso")
@EnableJpaRepositories("com.sas.sso")
@EnableWebSecurity
@PropertySource("classpath:application-redis-${spring.profiles.active}.properties")
@EnableRedisRepositories
public class SsoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoApplication.class, args);
		log.info("SSO-Application started successfully...");
	}

	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@PostConstruct
	void bcrypt()
	{
		String encode=passwordEncoder.encode("SANDEEP");
		String encod1e=passwordEncoder.encode("SANDEEP");
		
	}
}