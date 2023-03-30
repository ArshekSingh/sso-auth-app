package com.sas.sso.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RoleDTO {

	@NotNull
	Long roleId;

	@NotEmpty
	String roleName;
}
