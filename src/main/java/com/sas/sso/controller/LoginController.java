package com.sas.sso.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;
import com.sas.sso.service.UserService;

@Controller
public class LoginController {

	@Autowired
	UserService userService;

	@GetMapping(value = "auth/login")
	ModelAndView loginForm(@RequestParam(required = true) String appName,
			@RequestParam(required = false) String callBackUrl, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		if (httpServletRequest.getCookies() != null && httpServletRequest.getCookies().length > 0) {
			for (Cookie cookie : httpServletRequest.getCookies()) {
				if (cookie.getName().equals("token")) {

					return userService.redirectAuthenticatedUser(appName, cookie.getValue(), httpServletResponse,callBackUrl);

				}
			}
		}
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.setViewName("login");
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setAppName(appName);
		loginDTO.setCallBackUrl(callBackUrl);
		modelAndView.addObject("loginDTO", loginDTO);
		return modelAndView;
	}

	@PostMapping(value = "auth/login")
	ModelAndView loginFormPost(@ModelAttribute LoginDTO loginDTO, HttpServletResponse response) {
		return userService.authenticateUser(loginDTO, response);
	}
}
