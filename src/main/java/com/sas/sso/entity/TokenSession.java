package com.sas.sso.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
@RedisHash(value = "TOKEN_SESSION")
public class TokenSession implements Serializable {
	private final static long serialVersionUID = -6524768694427900654L;

	@Id
	@Indexed
	private String token;

	private String userId;
	
	private String companyId;
	
	private String companyCode;

	private String appId;
}