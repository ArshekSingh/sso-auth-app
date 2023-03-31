package com.sas.sso.service;

import com.sas.sso.dto.Response;
import com.sas.sso.request.TokenValidationRequest;

public interface TokenValidationService {
	
	Response validateToken(TokenValidationRequest tokenValidationRequest);

}
