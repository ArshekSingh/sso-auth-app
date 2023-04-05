package com.sas.sso.service;

import javax.servlet.http.HttpServletResponse;

import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserDto;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;

public interface UserService {

	
	ModelAndView authenticateUser(LoginDTO loginDTO,HttpServletResponse response);
	ModelAndView redirectAuthenticatedUser(String token,String string, HttpServletResponse response);

	Response createUser(UserDto userDto);

	Response updateUser(UserDto userDto);

	Response findByIdOrNameOrEmail(Long id, String name, String email);
	ModelAndView redirectAuthenticatedUser(String token,String string, HttpServletResponse response, String callBackUrl);
}
