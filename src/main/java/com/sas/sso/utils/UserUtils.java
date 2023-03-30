package com.sas.sso.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sas.sso.entity.User;

@Component
public class UserUtils {

	public User getLoggedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
