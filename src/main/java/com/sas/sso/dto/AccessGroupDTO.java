package com.sas.sso.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
public class AccessGroupDTO {


    Long accessGroupId;

    @NotEmpty
    String accessGroupName;

    @Valid
    @NotEmpty
    Set<RoleDTO> roles = new HashSet<>();

    
    boolean active;
    HashMap<String, Set<RoleDTO>> groupRoles = new HashMap<>();
    
    @NotNull
    @Min(value = 1)
    private Long compId;
    
    private String companyName;

    @NotNull
    @Min(value = 1)
    private Long appId;
    
    private String appName;
    private String description;

}
