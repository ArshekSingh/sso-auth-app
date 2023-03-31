package com.sas.sso.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sas.sso.dto.Response;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.UserSession;
import com.sas.sso.request.TokenValidationRequest;
import com.sas.sso.service.TokenValidationService;
import com.sas.sso.utils.UserUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenValidationServiceImpl implements TokenValidationService {

	@Autowired
	UserUtils userUtils;

	@Autowired
	JwtService jwtService;

	@Override
	public Response validateToken(TokenValidationRequest tokenValidationRequest) {

		Optional<TokenSession> tokenSesseionOptional = userUtils.findByToken(tokenValidationRequest.getToken());
		if (tokenSesseionOptional.isPresent()) {
			Response response = new Response();

			TokenSession tokenSession = tokenSesseionOptional.get();
			try {
				Optional<UserSession> userSessionOptional = userUtils.findById(tokenSession.getUserId());
				if (jwtService.isTokenValid(tokenValidationRequest.getToken(), userSessionOptional.get())) {
					Claims claims = jwtService.extractAllClaims(tokenValidationRequest.getToken());
					return Response.builder().data(claims).code(HttpStatus.OK.value()).status(HttpStatus.OK).build();
				} else {
					log.error("Invalid token supplied");
					response.setStatus(HttpStatus.UNAUTHORIZED);
					response.setCode(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Invalid Token");
				}
			} catch (ExpiredJwtException e) {
				log.error("ExpiredJwtException occurred , token expired : {}", e.getMessage(), e);
				response.setStatus(HttpStatus.UNAUTHORIZED);
				response.setCode(HttpStatus.UNAUTHORIZED.value());
				response.setMessage("Token has expired");

			} catch (SignatureException e) {
				log.error("SignatureException occurred , token expired : {}", e.getMessage(), e);
				response.setStatus(HttpStatus.UNAUTHORIZED);
				response.setCode(HttpStatus.CONFLICT.value());
				response.setMessage("Tampered token");

			}
			return response;
		} else {

			return Response.builder().code(HttpStatus.UNAUTHORIZED.value()).status(HttpStatus.UNAUTHORIZED)
					.message("Token does not exist").build();
		}
	}

}
