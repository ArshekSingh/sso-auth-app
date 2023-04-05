package com.sas.sso.dto;

import com.sas.sso.entity.AccessGroup;
import com.sas.sso.entity.CompanyMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private Set<AccessGroup> accessGroups;
    private CompanyMaster companyMaster;
}
