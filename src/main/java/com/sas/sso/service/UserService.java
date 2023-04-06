package com.sas.sso.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;
import com.sas.sso.dto.Response;

public interface UserService {

	
	ModelAndView authenticateUser(LoginDTO loginDTO,HttpServletResponse response);
	ModelAndView redirectAuthenticatedUser(String token,String string, HttpServletResponse response, String callBackUrl);
	
	Response logout();
}
