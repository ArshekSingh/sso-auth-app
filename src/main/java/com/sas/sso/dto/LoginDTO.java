package com.sas.sso.dto;

import lombok.Data;

@Data
public class LoginDTO {

	private String userName;
	private String password;
	private String appName;
	private String companyCode;
	private String callBackUrl;

}

