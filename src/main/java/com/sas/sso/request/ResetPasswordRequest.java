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
public class ResetPasswordRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String confirmPassword;
}
