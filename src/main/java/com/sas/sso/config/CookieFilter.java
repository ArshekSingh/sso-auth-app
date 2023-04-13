package com.sas.sso.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sas.sso.constants.RestMappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
public class CookieFilter implements Filter {

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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		boolean isValidRequest =true;
		
		if (httpServletRequest.getRequestURI().contains("/api/")  &&!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
			TokenSession tokenFromCookie = userUtils.getTokenSession();
			
			if(tokenFromCookie!=null ) {
				Optional<UserSession> userSessionOptional = userRedisRepository.findById(tokenFromCookie.getUserId());
				if (userSessionOptional.isPresent()&& jwtService.isTokenValid(tokenFromCookie.getToken(), userSessionOptional.get())) {
					log.info("token and user valid , passing to controller");
					
				} else {
					isValidRequest=false;
				}
			}
		}
		if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod()) && isValidRequest) {
			chain.doFilter(httpServletRequest, httpServletResponse);
		}
		else {
			if (httpServletRequest.getRequestURI().contains("/api/") && !"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
				httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.getWriter().write(objectMapper.writeValueAsString(new Response(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED)));
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			} else {
				httpServletResponse.setStatus(HttpStatus.OK.value());
				response.getWriter().write(objectMapper.writeValueAsString(new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK)));
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			}
			
		}
	}

}