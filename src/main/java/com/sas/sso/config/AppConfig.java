package com.sas.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sas.sso.intercepter.RequestInterceptor;
import com.sas.sso.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

	private final UserRepository repository;

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> repository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestInterceptor());
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
				.allowedHeaders("Authorization",
						"Accept",
						"Cache-Control",
						"Content-Type",
						"Origin",
						"x-csrf-token",
						"x-requested-with",
						"Access-Control-Allow-Origin","Access-Control-Allow-Headers","Access-Control-Allow-Credentials")
				.exposedHeaders("Authorization",
						"Accept",
						"Cache-Control",
						"Content-Type",
						"Origin",
						"x-csrf-token",
						"x-requested-with",
						"Access-Control-Allow-Origin","Access-Control-Allow-Headers","Access-Control-Allow-Credentials")
				.allowedOrigins("http://localhost:3000/","https://7036-13-232-90-28.ngrok-free.app").maxAge(4800);
	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:3000","https://7036-13-232-90-28.ngrok-free.app"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));

		// NOTE: setAllowCredentials(true) is important,
		// otherwise, the value of the 'Access-Control-Allow-Origin' header in the response
		// must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);

		// NOTE: setAllowedHeaders is important!
		// Without it, OPTIONS preflight request will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(Arrays.asList(
				"Authorization",
				"Accept",
				"Cache-Control",
				"Content-Type",
				"Origin",
				"x-csrf-token",
				"x-requested-with",
				"Access-Control-Allow-Origin","Access-Control-Allow-Headers","Access-Control-Allow-Credentials"
		));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
