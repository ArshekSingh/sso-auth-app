package com.sas.sso.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class RoleGroupDTO {


    private String roleGroupName;

    @Valid
    @NotEmpty
    Set<RoleDTO> roles = new HashSet<>();
}
