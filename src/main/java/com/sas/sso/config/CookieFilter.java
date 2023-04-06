package com.sas.sso.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sas.sso.dto.Response;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.UserSession;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.repository.UserRedisRepository;
import com.sas.sso.serviceimpl.JwtService;
import com.sas.sso.utils.UserUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CookieFilter extends OncePerRequestFilter {

	@Autowired
	TokenRedisRepository tokenRedisRepository;

	@Autowired
	UserRedisRepository userRedisRepository;

	@Autowired
	UserUtils userUtils;

	@Autowired
	JwtService jwtService;

	@Autowired
	ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		TokenSession tokenFromCookie = userUtils.getTokenSession();
		if (request.getRequestURI().contains("/api/") && tokenFromCookie != null) {

			log.info("Token Found in session registry");
			Optional<UserSession> userSessionOptional = userRedisRepository.findById(tokenFromCookie.getUserId());
			if (userSessionOptional.isPresent()
					&& jwtService.isTokenValid(tokenFromCookie.getToken(), userSessionOptional.get())) {
				log.info("token and user valid , passing to controller");
			} else {
				log.warn("token and user invalid , returning with 401");
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.getWriter().write(objectMapper.writeValueAsString(
						new Response(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED)));
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			}
		}

	}
}