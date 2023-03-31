package com.sas.sso.repository;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sas.sso.entity.TokenSession;

@Repository
@ConfigurationProperties(prefix = "redis")
public interface TokenRedisRepository extends CrudRepository<TokenSession, String> {

	Optional<TokenSession> findByToken(String token);

	boolean existsByToken(String token);

}
