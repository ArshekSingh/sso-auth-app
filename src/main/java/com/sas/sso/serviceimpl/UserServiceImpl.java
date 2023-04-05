package com.sas.sso.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.sas.sso.dao.UserDao;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserDto;
import com.sas.sso.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;
import com.sas.sso.repository.AppMasterRepository;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.service.UserService;
import com.sas.sso.utils.UserUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private static final int ONE_DAY_IN_SECONDS = 86400;

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

	@Autowired
	UserDao userDao;

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

				setCookie(response, tokenSession);
				return modelAndView;
			} else {
				log.error("user with id {} does not have any company with name {}", user.getId(),
						loginDTO.getAppName());
				modelAndView.setViewName("Login_v1/index");
				modelAndView.addObject("loginDTO", loginDTO);
				modelAndView.addObject("error_message", "App Does not exist in system");
			}

		} else {
			log.error("credentials failed to match with system ");
			modelAndView.setViewName("Login_v1/index");
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "Invalid Credentials");
		}
		return modelAndView;
	}

	private void setCookie(HttpServletResponse response, TokenSession tokenSession) {
		//Tue, 04-Apr-2023 10:15:01 GMT
		SimpleDateFormat cookieExpireFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
		cookieExpireFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, ONE_DAY_IN_SECONDS);
		String cookieLifeTime = cookieExpireFormat.format(cal.getTime());
		response.setHeader(HttpHeaders.SET_COOKIE,
				"token=".concat(tokenSession.getToken()).concat("; Path=/; Expires=" + cookieLifeTime + ";"));
	}

	@Override
	public ModelAndView redirectAuthenticatedUser(String appName, String token, HttpServletResponse response) {
		log.info("Cookie session detected , validating..");
		ModelAndView modelAndView = new ModelAndView();
		try {

			Claims claims = jwtService.extractAllClaims(token);
			HashMap<String, Object> companyClaims = (HashMap<String, Object>) claims.get("company");

			Optional<AppMaster> appMasterOptional = appMasterRepository.findByApplicationNameAndCompanyId(appName,
					Long.parseLong(companyClaims.get("companyId").toString()));
			if (appMasterOptional.isPresent()) {
				AppMaster appMaster = appMasterOptional.get();
				StringBuilder builder = new StringBuilder();
				builder.append("redirect:").append(appMaster.getBaseUrl()).append("?token=".concat(token));
				log.info("Redirecting with existing session");
				modelAndView.setViewName(builder.toString());
			} else {
				LoginDTO loginDTO = new LoginDTO();
				log.error("Company does not exist in system {}", appName);
				modelAndView.setViewName("Login_v1/index");
				modelAndView.addObject("loginDTO", loginDTO);
				modelAndView.addObject("error_message", "Token tampered , Invalid Token");
			}

		} catch (ExpiredJwtException e) {
			log.error("ExpiredJwtException occurred , token expired : {}", e.getMessage(), e);

			LoginDTO loginDTO = new LoginDTO();
			modelAndView.setViewName("Login_v1/index");
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "Token Expired");

		} catch (SignatureException e) {
			log.error("SignatureException occurred , token expired : {}", e.getMessage(), e);

			LoginDTO loginDTO = new LoginDTO();
			modelAndView.setViewName("Login_v1/index");
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "Token tampered , Invalid Token");

		}
		return modelAndView;
	}

	@Override
	public Response createUser(UserDto userDto) {
		log.info("request to create user initiatied successfully");
		Optional<User> userOptional = userRepository.findByEmail(userDto.getEmail());
		if(userOptional.isPresent()){
			log.info("user is already present with email ->{}",userDto.getEmail());
			return new Response("USER IS ALREADY PRESENT WITH THIS EMIAL ID", HttpStatus.BAD_REQUEST);
		}
		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setLastname(userDto.getLastname());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		//set company Master
		CompanyMaster companyMaster = new CompanyMaster();
		companyMaster.setCompanyName(userDto.getCompanyMaster().getCompanyName());
		companyMaster.setCompanyName(userDto.getCompanyMaster().getCompanyName());
		companyMaster.setWebsiteUrl(userDto.getCompanyMaster().getWebsiteUrl());
		user.setCompanyMaster(companyMaster);

		//set Access group
		for(AccessGroup accessGroup1 : userDto.getAccessGroups()) {
			AccessGroup accessGroup = new AccessGroup();
			accessGroup.setAccessGroupNo(accessGroup1.getAccessGroupNo());
			accessGroup.setDescription(accessGroup1.getDescription());
			accessGroup.setName(accessGroup1.getName());
			accessGroup.setActive(true);
			accessGroup.setEditable(accessGroup1.getEditable());
			accessGroup.setCreatedBy(accessGroup1.getCreatedBy());
			user.setAccessGroups((Set<AccessGroup>) accessGroup);
		}

		userRepository.save(user);
		log.info("User saved successfully");
		return  new Response("USER CREATED SUCCESSFULLY",HttpStatus.OK);
	}

	@Override
	public Response updateUser(UserDto userDto) {
		log.info("Request to update user invoked for emailId -> {}",userDto.getEmail());
		Optional<User> userOptional = userRepository.findByEmail(userDto.getEmail());
		if(userOptional.isEmpty()){
			log.info("User not found for this emailId ->{}",userDto.getEmail());
			return new Response("User not present for this email id -> {}",userDto.getEmail(),HttpStatus.BAD_REQUEST);
		}
		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastname(userDto.getLastname());
		user.setEmail(userDto.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		//set company Master
		CompanyMaster companyMaster = new CompanyMaster();
		companyMaster.setCompanyName(userDto.getCompanyMaster().getCompanyName());
		companyMaster.setCompanyName(userDto.getCompanyMaster().getCompanyName());
		companyMaster.setWebsiteUrl(userDto.getCompanyMaster().getWebsiteUrl());
		user.setCompanyMaster(companyMaster);

		//set Access group
		for(AccessGroup accessGrp : userDto.getAccessGroups()){
			AccessGroup accessGroup = new AccessGroup();
			accessGroup.setAccessGroupNo(accessGrp.getAccessGroupNo());
			accessGroup.setDescription(accessGrp.getDescription());
			accessGroup.setName(accessGrp.getName());
			accessGroup.setEditable(accessGrp.getEditable());
			user.setAccessGroups((Set<AccessGroup>) accessGroup);
		}

		userRepository.save(user);
		log.info("User updated successfully for emailId -> {}",userDto.getEmail());
		return new Response("User updated successfully",HttpStatus.OK);
	}

	@Override
	public Response findByIdOrNameOrEmail(Long id, String name, String email) {
		User userOptional = userDao.findUserByIdOrNameOrEmail(id,name,email);
		if(userOptional==null){
			return new Response("User Not Found",HttpStatus.BAD_REQUEST);
		}
		User user = new User();
		user.setId(userOptional.getId());
		user.setEmail(userOptional.getEmail());
		user.setFirstName(userOptional.getFirstName());
		user.setLastname(userOptional.getLastname());
		//get company Master
		CompanyMaster companyMaster = new CompanyMaster();
		companyMaster.setCompanyName(userOptional.getCompanyMaster().getCompanyName());
		companyMaster.setCompanyName(userOptional.getCompanyMaster().getCompanyName());
		companyMaster.setWebsiteUrl(userOptional.getCompanyMaster().getWebsiteUrl());
		user.setCompanyMaster(companyMaster);

		//get Access group
		for(AccessGroup accessGrp : userOptional.getAccessGroups()){
			AccessGroup accessGroup = new AccessGroup();
			accessGroup.setAccessGroupNo(accessGrp.getAccessGroupNo());
			accessGroup.setDescription(accessGrp.getDescription());
			accessGroup.setName(accessGrp.getName());
			accessGroup.setEditable(accessGrp.getEditable());
			user.setAccessGroups((Set<AccessGroup>) accessGroup);
		}
		return new Response("User found successfully",user,HttpStatus.OK);

	}
}
