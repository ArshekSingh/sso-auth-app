package com.sas.sso.dao;

import java.util.List;

import com.sas.sso.entity.User;
import com.sas.sso.request.UserRequest;

public interface UserDao {
	
    List<User> getUserDetailsByFilter(UserRequest request);
}
