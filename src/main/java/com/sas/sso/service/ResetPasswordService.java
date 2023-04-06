package com.sas.sso.service;

import com.sas.sso.dto.Response;
import com.sas.sso.request.ResetPasswordRequest;

import javax.servlet.http.HttpServletRequest;

public interface ResetPasswordService {
    Response resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest);
}
