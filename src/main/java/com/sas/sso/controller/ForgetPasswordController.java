package com.sas.sso.controller;

import com.sas.sso.dto.ForgetPasswordDto;
import com.sas.sso.dto.VerifyOtpDto;
import com.sas.sso.exception.InternalServerErrorException;
import com.sas.sso.request.CreateNewPasswordRequest;
import com.sas.sso.service.ForgetPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class ForgetPasswordController {
    @Autowired
    private ForgetPasswordService passwordService;

    @GetMapping("/auth/forgetPassword")
    public ModelAndView forgetPasswordGet() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("forgetPasswordDto", new ForgetPasswordDto());
        modelAndView.setViewName("Login_v1/forgetPassword");
        return modelAndView;
    }

    @PostMapping("/auth/forgetPassword")
    public ModelAndView forgetPassword(@ModelAttribute ForgetPasswordDto forgetPasswordDto) throws InternalServerErrorException {
        log.info("Request initiated to forget password for email id {} and company code {}", forgetPasswordDto.getUserName(), forgetPasswordDto.getCompanyCode());
        return passwordService.forgetPassword(forgetPasswordDto);
    }

    @PostMapping("/auth/verifyOtp")
    public ModelAndView verifyOtp(@ModelAttribute VerifyOtpDto dto) {
        log.info("Request initiated to verify OTP for user {}", dto.getUserName());
        return passwordService.verifyOtp(dto);
    }

    @PostMapping("/auth/updatePassword")
    public ModelAndView createNewPassword(@ModelAttribute CreateNewPasswordRequest createNewPasswordRequest) {
        log.info("Request initiated to updatePassword for userId : {}", createNewPasswordRequest.getUserName());
        return passwordService.updatePassword(createNewPasswordRequest);
    }

}
