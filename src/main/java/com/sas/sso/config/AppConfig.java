package com.sas.sso.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sas.sso.intercepter.RequestInterceptor;
import com.sas.sso.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
	
	@Value("${app.allowed.origins}")
	List<String> allowedOrigins;
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);

		allowedOrigins.parallelStream().forEach(str -> config.addAllowedOrigin(str));
		config.addAllowedHeader("*");

		config.addAllowedMethod("*");

		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(

				new CorsFilter(source));

		bean.setOrder(0);

		return bean;

	}
}
