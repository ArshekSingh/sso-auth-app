package com.sas.sso.controller;

import com.sas.sso.dto.ForgetPasswordDto;
import com.sas.sso.dto.Response;
import com.sas.sso.exception.BadRequestException;
import com.sas.sso.exception.InternalServerErrorException;
import com.sas.sso.request.CreateNewPasswordRequest;
import com.sas.sso.request.ResetPasswordRequest;
import com.sas.sso.service.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping("/auth")
public class PasswordManagementController {
    @Autowired
    private PasswordService passwordService;

    @PostMapping("/forgetPassword")
    public ModelAndView forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto) throws InternalServerErrorException {
        log.info("Request initiated to forget password for email id {} and company code {}", forgetPasswordDto.getUserName(), forgetPasswordDto.getCompanyCode());
        return passwordService.forgetPassword(forgetPasswordDto);
    }

//    @PostMapping("/verifyOtp")
//    public Response verifyOtp(@RequestParam String email, @RequestParam String otp) {
//        log.info("Request initiated to verify OTP for user {}", email);
//        return passwordService.verifyOtp(email, otp);
//    }
//
//    @PostMapping("/updatePassword")
//    public Response createNewPassword(@Valid @RequestBody CreateNewPasswordRequest createNewPasswordRequest) throws BadRequestException {
//        log.info("Request initiated to updatePassword for userId : {}", createNewPasswordRequest.getUserId());
//        return passwordService.updatePassword(createNewPasswordRequest);
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PostMapping("/api/resetPassword")
//    public Response resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
//        log.info("resetPassword request received , userId : {} ", request.getEmail());
//        return passwordService.resetPassword(request, httpServletRequest);
//    }

//    @PostMapping("/api/changePassword")
//    public Response changePassword(HttpServletRequest httpServletRequest, @RequestBody ChangePasswordRequest request) throws ObjectNotFoundException, BadRequestException {
//        log.info("changePassword request received , userId : {} ", request.getEmail());
//        Response responseEntity = passwordService.changePassword(request, httpServletRequest);
//        passwordService.logout(httpServletRequest);
//        return responseEntity;
//    }

}
