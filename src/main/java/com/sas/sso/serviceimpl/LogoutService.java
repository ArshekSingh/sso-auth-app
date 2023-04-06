package com.sas.sso.serviceimpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sas.sso.dto.Response;
import com.sas.sso.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

	@Autowired
	UserService userService;

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Response responseBody = userService.logout();
		try {
			response.getWriter().write(objectMapper.writeValueAsString(responseBody));

		} catch (Exception e) {
			log.error("Exception occurred , message : {}", e.getLocalizedMessage(), e);
		}
		response.setStatus(responseBody.getCode());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

	}
}
