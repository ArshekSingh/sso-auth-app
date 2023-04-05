package com.sas.sso.serviceimpl;


import com.sas.sso.components.SmsProperties;
import com.sas.sso.constants.Constant;
import com.sas.sso.constants.RestMappingConstants;
import com.sas.sso.dto.Response;
import com.sas.sso.entity.User;
import com.sas.sso.entity.UserSession;
import com.sas.sso.entity.VendorSmsLog;
import com.sas.sso.exception.BadRequestException;
import com.sas.sso.exception.InternalServerErrorException;
import com.sas.sso.exception.ObjectNotFoundException;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.repository.VendorSmsLogRepository;
import com.sas.sso.request.ChangePasswordRequest;
import com.sas.sso.request.CreateNewPasswordRequest;
import com.sas.sso.request.ResetPasswordRequest;
import com.sas.sso.service.PasswordService;
import com.sas.sso.utils.SmsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;


@Service
@Slf4j
@AllArgsConstructor
public class PasswordServiceImpl implements PasswordService, Constant {
    private final UserRepository userRepository;
    private final SmsUtil smsUtil;
    private final SmsProperties smsProperties;
    private final VendorSmsLogRepository vendorSmsLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ModelAndView forgetPassword(String userName, String companyCode) throws InternalServerErrorException {
        if (!StringUtils.hasText(userName)) {
            log.warn("Email id cannot be empty");
            return new Response("Email id cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if(!StringUtils.hasText(companyCode)) {
            log.warn("Company code cannot be empty");
            return new Response("Company code cannot be empty", HttpStatus.BAD_REQUEST);
        }
        String otp = RandomStringUtils.randomNumeric(6);
        String message = "Use OTP " + otp + " to reset your SVCL-FINNCUB password. Do not share the OTP or your number with anyone-SV Creditline Ltd";
        User user = getUser(userName);
        if (user == null) {
            log.error("No user details found against email id {}", userName);
            return new Response(NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (!StringUtils.hasText(user.getMobile())) {
            log.error("Mobile is not mapped with the email {}", userName);
            return new Response("Mobile is not mapped with the email " + userName, HttpStatus.NOT_FOUND);
        }
        getVendorSmsLog(otp, message, user);
        log.info("Otp sent to the registered mobile number {}", otp);
        return new Response("Otp sent to the registered mobile number", HttpStatus.OK);

    }

    @Override
    @Transactional
    public Response verifyOtp(String email, String otp) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(otp)) {
            log.error("OTP cannot be empty.");
            return new Response("OTP cannot be empty", HttpStatus.BAD_REQUEST);
        }
        // first check data is available in database
        User user = getUser(email);
        if (user == null) {
            log.error("User details not found for user id {}", email);
            return new Response("User details not found for user id {}" + email, HttpStatus.NOT_FOUND);
        }
        Optional<VendorSmsLog> vendorSmsLog = vendorSmsLogRepository.findTop1BySmsMobileAndStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getMobile(), "D", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        // otp check
        if (vendorSmsLog.isPresent() && otp.equalsIgnoreCase(vendorSmsLog.get().getSmsOtp())) {
            vendorSmsLog.get().setStatus("U");    // U is for USED status
            vendorSmsLog.get().setModifiedBy(email);
            vendorSmsLog.get().setModifiedOn(LocalDateTime.now());
            vendorSmsLogRepository.save(vendorSmsLog.get());
            user.setIsOtpValidated("Y");
            userRepository.save(user);
            log.info("OTP verified successfully for userId {}", email);
            return new Response("OTP verified successfully", HttpStatus.OK);
        } else {
            log.error("Invalid Otp is entered by the user {}", email);
            return new Response("Invalid Otp entered by the user " + email, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public Response updatePassword(CreateNewPasswordRequest createNewPasswordRequest) throws BadRequestException {
        if (!createNewPasswordRequest.getNewPassword().equals(createNewPasswordRequest.getConfirmPassword())) {
            log.error("New password is not same as confirm password for userId : {} ", createNewPasswordRequest.getUserId());
            throw new BadRequestException("New password is not same as confirm password ", HttpStatus.BAD_REQUEST);
        }
        if (createNewPasswordRequest.getNewPassword().length() < 5) {
            log.error("Minimum length of new password should be at least 5 characters");
            throw new BadRequestException("Minimum length of new password should be at least 5 characters", HttpStatus.BAD_REQUEST);
        }
        User user = getUser(createNewPasswordRequest.getUserId());
        if (user == null) {
            return new Response("No user detail found against email id " + createNewPasswordRequest.getUserId(), HttpStatus.NOT_FOUND);
        }
        Optional<VendorSmsLog> vendorSmsLog = vendorSmsLogRepository.findTop1BySmsMobileAndStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getMobile(), "U", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        if (vendorSmsLog.isPresent() && createNewPasswordRequest.getOtp().equalsIgnoreCase(vendorSmsLog.get().getSmsOtp())) {
            user.setPassword(passwordEncoder.encode(createNewPasswordRequest.getNewPassword()));
            user.setIsPasswordActive("Y");
            vendorSmsLog.get().setStatus("E");
            vendorSmsLogRepository.save(vendorSmsLog.get());
            user.setUpdatedOn(new Date());
            user.setUpdatedBy(Long.valueOf(createNewPasswordRequest.getUserId()));
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", createNewPasswordRequest.getUserId());
            return new Response("Password reset was successful", HttpStatus.OK);
        } else {
            log.error("Otp is not verified.");
            return new Response("Otp is not verified.", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    @Transactional
    public Response resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        Response response = new Response();
        //Logged in admin details
        String authorization = httpServletRequest.getHeader("Authorization");
        String jwtToken = authorization.substring(7);
        String adminEmail = jwtService.extractUsername(jwtToken);
        if (!adminEmail.isEmpty()) {
            log.info("Fetching user for resetPassword request, userId : {} ", request.getEmail());
            User user = getUser(request.getEmail());
            if (user == null) {
                log.error("User not found with email id {}", request.getEmail());
                return new Response("User not found with email id " + request.getEmail(), HttpStatus.NOT_FOUND);
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setIsPasswordActive("Y");
            user.setUpdatedOn(new Date());
            user.setUpdatedBy(Long.valueOf(adminEmail));
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", request.getEmail());
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Password changed successfully");
            return response;
        }
        log.error("Invalid token details");
        return new Response("INVALID_TOKEN", HttpStatus.BAD_REQUEST);
    }

    private void getVendorSmsLog(String otp, String message, User user) throws InternalServerErrorException {
        VendorSmsLog vendorSmsLogData = new VendorSmsLog();
        vendorSmsLogData.setSmsMobile(user.getMobile());
        vendorSmsLogData.setSmsText(message);
        vendorSmsLogData.setSmsType("FORGET"); // FORGET is for FORGET type
        vendorSmsLogData.setStatus("S"); // S is for SENT status
        vendorSmsLogData.setSmsOtp(otp);
        vendorSmsLogData.setSmsVendor("SMSJUST");
        vendorSmsLogData.setCreatedBy(user.getEmail());
        vendorSmsLogData.setCreatedOn(LocalDateTime.now());
        vendorSmsLogData = vendorSmsLogRepository.save(vendorSmsLogData);
        // hit sms API
        String responseId = smsUtil.sendSms(user.getMobile(), message);
        // update response id in VendorSmsLog returned from API
        if (StringUtils.hasText(responseId)) {
            vendorSmsLogData.setStatus("D");  // Status D is for Delivered.
            vendorSmsLogData.setSmsResponse(responseId);
            vendorSmsLogRepository.save(vendorSmsLogData);
            log.info("Message delivered to user {}", user.getEmail());
        } else {
            throw new InternalServerErrorException("Empty response received from vendor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User getUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

//    @Override
//    public Response changePassword(ChangePasswordRequest request, HttpServletRequest httpServletRequest) throws ObjectNotFoundException, BadRequestException {
//        Response response = new Response();
//        log.info("Fetching userSession for changePassword request, userId : {} ", request.getEmail());
////        UserSession userSession = userCredentialService.getUserSession();
////      validate password(Regex)
//        String authorization = httpServletRequest.getHeader("Authorization");
//        String jwtToken = authorization.substring(7);
//        String username = jwtService.extractUsername(jwtToken);
//        User user = userRepository.findByUserIdIgnoreCase(userSession.getUserId()).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
////      check current password
//        if (!user.isPasswordCorrect(request.getPassword())) {
//            log.error("Incorrect password supplied , userId : {}", request.getUserId());
//            throw new BadRequestException("Invalid current password", HttpStatus.BAD_REQUEST);
//        }
////      check new password with 5 old password
//        String oldPassword = user.getOldPassword();
//        if (oldPassword == null) {
//            oldPassword = user.getPassword();
//        } else {
//            String PASSWORD_SEPARATOR = ",,";
//            String[] oldPasswordList = oldPassword.split(PASSWORD_SEPARATOR);
//            for (String pass : oldPasswordList) {
//                if (BCrypt.checkpw(request.getNewPassword(), pass)) {
//                    log.error("New password matches with recent passwords  , userId : {}", request.getUserId());
//                    throw new BadRequestException("New password matches with recent passwords ", HttpStatus.BAD_REQUEST);
//                }
//            }
////          Maintain old passwords
//            if (oldPasswordList.length < oldPasswordCount) {
//                oldPassword = oldPassword + PASSWORD_SEPARATOR + user.getPassword();
//            } else {
//                StringBuilder updatedOldPassword = new StringBuilder();
//                for (int i = 1; i < oldPasswordList.length; i++) {
//                    if (updatedOldPassword.length() == 0) {
//                        updatedOldPassword = new StringBuilder(oldPasswordList[i]);
//                    } else {
//                        updatedOldPassword.append(PASSWORD_SEPARATOR).append(oldPasswordList[i]);
//                    }
//                }
//                oldPassword = updatedOldPassword + PASSWORD_SEPARATOR + user.getPassword();
//            }
//        }
////      update new password
//        user.setOldPassword(oldPassword);
//        user.setPassword(passwordEncoder, request.getNewPassword());
//        user.setIsTemporaryPassword("N");
//        user.setUpdatedOn(LocalDateTime.now());
//        user.setUpdatedBy(userSession.getUserId());
//        userRepository.save(user);
////        userRedisRepository.deleteById(userSession.g);
//        log.info("Password updated successfully , userId : {}", request.getUserId());
//        response.setCode(HttpStatus.OK.value());
//        response.setStatus(HttpStatus.OK);
//        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
//        return ResponseEntity.ok(response);
//    }

//    @Override
//    public Response logout(HttpServletRequest request) {
//        Response response = new Response();
//        String tokenString = request.getHeader("Authorization");
//        String token = tokenString.split(" ")[1];
//        template.delete(KEY + ":" + token);
//        log.info("logout successful");
//        UserLoginLog loginLog = userLoginLogRepository.findByTokenId(token);
//        if (loginLog != null) {
//            loginLog.setLogoutTime(LocalDateTime.now());
//            userLoginLogRepository.save(loginLog);
//            log.info("Token marked expired in db for TOKEN {}", token);
//        }
//        return response;
//    }
}
