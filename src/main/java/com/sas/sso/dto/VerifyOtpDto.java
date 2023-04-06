package com.sas.sso.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpDto {
    private String userName;
    private String companyCode;
    private String otp;

    public VerifyOtpDto(String userName, String companyCode) {
        this.userName = userName;
        this.companyCode = companyCode;
    }
}
