package com.sas.sso.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty
    Boolean active;
    HashMap<String, Set<RoleDTO>> groupRoles = new HashMap<>();
    @NotEmpty
    private Long copmId;
    private String companyName;
    @NotEmpty
    private Long appId;
    private String appName;
    private String description;

}
