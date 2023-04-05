package com.sas.sso.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;

@Data
@Slf4j
public class CreateNewPasswordRequest {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String otp;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String confirmPassword;
}
