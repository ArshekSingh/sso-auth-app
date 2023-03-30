package com.sas.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;

@Controller
public class LoginController {

	@GetMapping(value = "auth/login")
	ModelAndView loginForm(@RequestParam(required = true) String appName) {

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.setViewName("login");
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setAppName(appName);
		modelAndView.addObject("loginDTO", loginDTO);
		return modelAndView;
	}

	@PostMapping(value = "auth/login")
	ModelAndView loginFormPost(@ModelAttribute LoginDTO loginDTO) {

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.setViewName("login");
		return modelAndView;
	}
}
