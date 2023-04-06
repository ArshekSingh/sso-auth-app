package com.sas.sso.controller;

import com.sas.sso.dto.Response;
import com.sas.sso.request.ResetPasswordRequest;
import com.sas.sso.service.ResetPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService passwordService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/api/resetPassword")
    public Response resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        log.info("resetPassword request received , userId : {} ", request.getEmail());
        return passwordService.resetPassword(request, httpServletRequest);
    }
}
