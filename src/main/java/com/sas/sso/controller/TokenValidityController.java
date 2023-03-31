package com.sas.sso.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sas.sso.dto.Response;
import com.sas.sso.request.TokenValidationRequest;
import com.sas.sso.service.TokenValidationService;

@RestController
public class TokenValidityController {

	@Autowired
	TokenValidationService tokenValidationService;

	@PostMapping(value = "auth/validate_token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Response validateToken(@Valid @RequestBody TokenValidationRequest tokenValidationRequest) {
		return tokenValidationService.validateToken(tokenValidationRequest);

	}

}
