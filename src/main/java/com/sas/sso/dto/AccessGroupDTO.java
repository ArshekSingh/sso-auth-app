package com.sas.sso.dto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    private List<AccessGroupRoleDto> accessGroupRoleDtos;

}
