package com.sas.sso.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sas.sso.entity.UserSession;


@Repository
@ConfigurationProperties(prefix = "redis")
public interface UserRedisRepository extends CrudRepository<UserSession, String> {

}
