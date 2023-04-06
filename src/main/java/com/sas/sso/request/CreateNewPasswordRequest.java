package com.sas.sso.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewPasswordRequest {
    private String userName;
    private String otp;
    private String newPassword;
    private String confirmPassword;
    private String companyCode;

    public CreateNewPasswordRequest(String userName, String companyCode, String otp) {
        this.userName = userName;
        this.companyCode = companyCode;
        this.otp = otp;
    }
}
