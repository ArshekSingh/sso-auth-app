package com.sas.sso.dao;

import com.sas.sso.entity.User;

public interface UserDao {
    User findUserByIdOrNameOrEmail(Long id, String name, String email);
}
