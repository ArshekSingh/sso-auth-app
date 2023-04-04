package com.sas.sso.utils;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.User;
import com.sas.sso.entity.UserSession;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.repository.UserRedisRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserUtils {

	@Autowired
	UserRedisRepository userRedisRepository;

	@Autowired
	TokenRedisRepository tokenRedisRepository;

	public User getLoggedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public UserSession addUserToSessionCache(User user) {

		log.info("adding user to session , userId {}", user.getId());
		UserSession userSession = new UserSession();
		userSession.setEmail(user.getEmail());
		userSession.setId(user.getId().toString());
		userSession.setName(user.getFirstName().concat(" ").concat(user.getLastname()));
		return userRedisRepository.save(userSession);
	}

	public void removeUserFromSessionCache(UserSession userSession) {

		log.info("removing user from session , userId {}", userSession.getId());
		userRedisRepository.delete(userSession);
	}

	public void removeUserFromSessionCacheById(String userId) {

		Optional<UserSession> userSessionOptional = userRedisRepository.findById(userId);

		if (userSessionOptional.isPresent()) {
			log.info("removing user from session , userId {}", userId);
			userRedisRepository.delete(userSessionOptional.get());

		}
	}

	public boolean existByToken(String token) {

		return tokenRedisRepository.existsByToken(token);
	}

	public Optional<TokenSession> findByToken(String token) {

		return tokenRedisRepository.findByToken(token);
	}

	public Optional<UserSession> findById(String id) {

		return userRedisRepository.findById(id);
	}

	public TokenSession addTokenToCache(String token, User user) {
		TokenSession tokenSession = new TokenSession();
		tokenSession.setToken(token);
		tokenSession.setUserId(user.getId().toString());
		tokenSession.setCompanyId(user.getCompanyMaster().getCompanyId().toString());
		tokenSession.setCompanyCode(user.getCompanyMaster().getCompanyCode());
		return tokenRedisRepository.save(tokenSession);
	}

	public void removeTokenFromTokenCacheByToken(String token) {

		Optional<TokenSession> tokenSessionOptional = tokenRedisRepository.findByToken(token);

		if (tokenSessionOptional.isPresent()) {
			log.info("removing token from cache , userId {}", tokenSessionOptional.get().getUserId());
			tokenRedisRepository.delete(tokenSessionOptional.get());

		}
	}
	
	public Optional<String> getAuthTokenFromRequest() {
		HttpServletRequest httpServletRequest = null;
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

		}

		if (httpServletRequest != null && httpServletRequest.getCookies() != null
				&& httpServletRequest.getCookies().length > 0) {
			for (Cookie cookie : httpServletRequest.getCookies()) {
				if (cookie.getName().equals("token")) {

					return Optional.of(cookie.getValue());

				}
			}
		}
		return Optional.empty();
	}
	
	public TokenSession getTokenSession() {
		try {
			Optional<String> jwtToken = getAuthTokenFromRequest();
			Optional<TokenSession> tokenSession = tokenRedisRepository.findByToken(jwtToken.get());
			if (tokenSession.isPresent()) {
				return tokenSession.get();
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}
