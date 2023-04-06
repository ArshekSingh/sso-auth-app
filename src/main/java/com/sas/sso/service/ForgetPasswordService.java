package com.sas.sso.service;

import com.sas.sso.dto.ForgetPasswordDto;
import com.sas.sso.dto.VerifyOtpDto;
import com.sas.sso.request.CreateNewPasswordRequest;
import org.springframework.web.servlet.ModelAndView;

public interface ForgetPasswordService {
    ModelAndView forgetPassword(ForgetPasswordDto forgetPasswordDto);

    ModelAndView verifyOtp(VerifyOtpDto dto);

    ModelAndView updatePassword(CreateNewPasswordRequest createNewPasswordRequest);

}
