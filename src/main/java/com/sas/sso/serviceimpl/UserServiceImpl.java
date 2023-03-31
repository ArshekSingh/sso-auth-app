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
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.User;
import com.sas.sso.repository.AppMasterRepository;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.service.UserService;
import com.sas.sso.utils.UserUtils;

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

	@Autowired
	UserUtils userUtils;

	@Override
	public ModelAndView authenticateUser(LoginDTO loginDTO, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		Optional<User> userOptional = userRepository.findByEmailAndCompanyMaster_CompanyCode(loginDTO.getUserName(),
				loginDTO.getCompanyCode());
		if (userOptional.isPresent()
				&& bCryptPasswordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {

			User user = userOptional.get();
			log.info("user with id {} found with supplied credentials", user.getId());

			CompanyMaster companyMaster = user.getCompanyMaster();
			Optional<AppMaster> appMasterOptional = appMasterRepository
					.findByApplicationNameAndCompanyId(loginDTO.getAppName(), companyMaster.getCompanyId());

			if (appMasterOptional.isPresent()) {

				userUtils.addUserToSessionCache(user);
				TokenSession tokenSession = userUtils.addTokenToCache(jwtService.generateToken(user),
						user.getId().toString());
				AppMaster appMaster = appMasterOptional.get();
				StringBuilder builder = new StringBuilder();
				builder.append("redirect:").append(appMaster.getBaseUrl())
						.append("?token=".concat(tokenSession.getToken()));
				modelAndView.setViewName(builder.toString());
				Cookie cookie = new Cookie("token", tokenSession.getToken());
				response.setHeader(HttpHeaders.SET_COOKIE, "token=".concat( tokenSession.getToken()).concat("; Path=/; Expires=Thu, 28 Mar 2024 12:14:27 GMT;"));
				return modelAndView;
			} else {
				log.error("user with id {} does not have any company with name {}", user.getId(),
						loginDTO.getAppName());
				modelAndView.setViewName("login");
				modelAndView.addObject("loginDTO", loginDTO);
				modelAndView.addObject("error_message", "no such app exists for this user");
			}

		} else {
			log.error("credentials failed to match with system ");
			modelAndView.setViewName("login");
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "invalid credentials");
		}
		return modelAndView;
	}

}
