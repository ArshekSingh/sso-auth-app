package com.sas.sso.serviceimpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			return;
		}
		String jwtToken = header.substring(7);
		/*
		 * Optional<Token> tokenOptional = tokenRepository.findByToken(jwtToken); if
		 * (tokenOptional.isPresent()) { Token token = tokenOptional.get();
		 * token.setExpired(true); token.setRevoked(true); tokenRepository.save(token);
		 * SecurityContextHolder.clearContext(); }
		 */
	}
}
