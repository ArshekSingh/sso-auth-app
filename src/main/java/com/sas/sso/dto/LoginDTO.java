package com.sas.sso.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

	private String userName;
	private String password;
	private String appName;
	private String companyCode;
	private String callBackUrl;

	public LoginDTO(String userName, String companyCode) {
		this.userName = userName;
		this.companyCode = companyCode;
	}

}

