package com.sas.sso.serviceimpl;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;
import com.sas.sso.entity.AppMaster;
import com.sas.sso.entity.CompanyMaster;
import com.sas.sso.entity.User;
import com.sas.sso.repository.AppMasterRepository;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	JwtService jwtService;

	@Autowired
	AppMasterRepository appMasterRepository;

	@Override
	public ModelAndView authenticateUser(LoginDTO loginDTO,HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		Optional<User> userOptional = userRepository.findByEmailAndCompanyMaster_CompanyCode(loginDTO.getUserName(),
				loginDTO.getCompanyCode());
		if (userOptional.isPresent()
				&& bCryptPasswordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
			User user = userOptional.get();
			String jwtString = jwtService.generateToken(user);
			CompanyMaster companyMaster = user.getCompanyMaster();
			Optional<AppMaster> appMasterOptional = appMasterRepository
					.findByApplicationNameAndCompanyId(loginDTO.getAppName(), companyMaster.getCompanyId());
			AppMaster appMaster = appMasterOptional.get();
			StringBuilder builder = new StringBuilder();
			builder.append("redirect:").append("https://dev.aiqahealth.com/enrollment-service/actuator/health").append("?token=".concat(jwtString));
			modelAndView.setViewName(builder.toString());
			Cookie cookie=new Cookie("token", jwtString);
			response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());

		}
		else
		{

		modelAndView.setViewName("login");
		modelAndView.addObject("loginDTO", loginDTO);
		modelAndView.addObject("error_message", "invalid credentials");
		}
		return modelAndView;
	}

}
