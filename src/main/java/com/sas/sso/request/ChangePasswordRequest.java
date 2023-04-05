package com.sas.sso.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
    private String appName;
    @NotEmpty
    private String companyCode;
}
