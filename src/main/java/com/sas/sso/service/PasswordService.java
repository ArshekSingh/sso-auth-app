package com.sas.sso.service;

import com.sas.sso.dto.Response;
import com.sas.sso.exception.BadRequestException;
import com.sas.sso.exception.InternalServerErrorException;
import com.sas.sso.exception.ObjectNotFoundException;
import com.sas.sso.request.ChangePasswordRequest;
import com.sas.sso.request.CreateNewPasswordRequest;
import com.sas.sso.request.ResetPasswordRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public interface PasswordService {
    ModelAndView forgetPassword(String userName, String companyCode) throws InternalServerErrorException;

    Response verifyOtp(String email, String otp);

    Response updatePassword(CreateNewPasswordRequest createNewPasswordRequest) throws BadRequestException;

    Response resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest);

//    Response changePassword(ChangePasswordRequest password, HttpServletRequest httpServletRequest) throws ObjectNotFoundException, BadRequestException;

//    Response logout(HttpServletRequest request);
}
