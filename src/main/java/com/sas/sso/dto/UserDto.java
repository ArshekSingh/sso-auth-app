package com.sas.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private Long compId;
    private String compName;
    private Long appId;
    private String appName;
	private String mobile;
	private String dob;
	private boolean active;
	private String isPasswordActive;
	private String isOtpValidated;
}
