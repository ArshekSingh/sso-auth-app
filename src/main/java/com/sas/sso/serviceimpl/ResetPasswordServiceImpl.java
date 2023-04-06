package com.sas.sso.serviceimpl;

import com.sas.sso.constants.Constant;
import com.sas.sso.dto.Response;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.User;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.request.ResetPasswordRequest;
import com.sas.sso.service.ResetPasswordService;
import com.sas.sso.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService, Constant {

    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Response resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        //Logged in admin details
        TokenSession tokenSession = userUtils.getTokenSession();
        String adminEmail = tokenSession.getUserId();
        String companyCode = tokenSession.getCompanyCode();
        if (!adminEmail.isEmpty()) {
            log.info("Fetching user for resetPassword request, userId : {} ", request.getEmail());
            User user = getUser(request.getEmail(), companyCode);
            if (user == null) {
                log.error("User not found with email id {}", request.getEmail());
                return new Response("User not found with email id " + request.getEmail(), HttpStatus.NOT_FOUND);
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setIsPasswordActive("Y");
            user.setUpdatedOn(new Date());
            user.setUpdatedBy(adminEmail);
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", request.getEmail());
            return new Response("Password changed successfully", HttpStatus.OK);
        }
        log.error("Invalid token details");
        return new Response("INVALID_TOKEN", HttpStatus.BAD_REQUEST);
    }

    private User getUser(String email, String companyCode) {
        Optional<User> user = userRepository.findByEmailAndCompanyMaster_CompanyCode(email, companyCode);
        return user.orElse(null);
    }
}
