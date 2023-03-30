package com.sas.sso.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@RedisHash(value = "USER_SESSION", timeToLive = 1800)
public class UserSession implements Serializable {
	private final static long serialVersionUID = -6524768694427900654L;
	private String id;
	private String name;
	private String email;
	private Set<String> roles = new HashSet<>();

}