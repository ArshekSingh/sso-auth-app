package com.sas.sso.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.SimpleTimeZone;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sas.sso.dto.LoginDTO;
import com.sas.sso.dto.Response;
import com.sas.sso.entity.AppMaster;
import com.sas.sso.entity.CompanyMaster;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.User;
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
						user);
				
				AppMaster appMaster = appMasterOptional.get();
				
				StringBuilder builder = new StringBuilder();
				
				if (StringUtils.hasLength(loginDTO.getCallBackUrl()))
					builder.append("redirect:").append(loginDTO.getCallBackUrl())
							.append("?token=".concat(tokenSession.getToken()));
				else
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
	public ModelAndView redirectAuthenticatedUser(String appName, String token, HttpServletResponse response,String callBackUrl) {
		log.info("Cookie session detected , validating..");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("Login_v1/index");
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setAppName(appName);
		try {

			Claims claims = jwtService.extractAllClaims(token);
			HashMap<String, Object> companyClaims = (HashMap<String, Object>) claims.get("company");

			Optional<AppMaster> appMasterOptional = appMasterRepository.findByApplicationNameAndCompanyId(appName,
					Long.parseLong(companyClaims.get("companyId").toString()));
			if (appMasterOptional.isPresent()) {
				AppMaster appMaster = appMasterOptional.get();
				StringBuilder builder = new StringBuilder();
				if(StringUtils.hasLength(callBackUrl))
					builder.append("redirect:").append(callBackUrl).append("?token=".concat(token));
				else
					builder.append("redirect:").append(appMaster.getBaseUrl()).append("?token=".concat(token));
					
				log.info("Redirecting with existing session");
				modelAndView.setViewName(builder.toString());
			} else {
				
				log.error("Company does not exist in system {}", appName);
				modelAndView.addObject("loginDTO", loginDTO);
				modelAndView.addObject("error_message", "Token tampered , Invalid Token");
			}

		} catch (ExpiredJwtException e) {
			log.error("ExpiredJwtException occurred , token expired : {}", e.getMessage(), e);
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "Token Expired");

		} catch (SignatureException e) {
			log.error("SignatureException occurred , token expired : {}", e.getMessage(), e);
			modelAndView.addObject("loginDTO", loginDTO);
			modelAndView.addObject("error_message", "Token tampered , Invalid Token");

		}
		return modelAndView;
	}

	@Override
	public Response logout() {
		return userUtils.removeThisSessionToken();
	}

}
