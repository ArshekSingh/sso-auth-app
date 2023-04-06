package com.sas.sso.serviceimpl;


import com.sas.sso.components.EmailProperties;
import com.sas.sso.components.SmsProperties;
import com.sas.sso.constants.Constant;
import com.sas.sso.dto.ForgetPasswordDto;
import com.sas.sso.dto.LoginDTO;
import com.sas.sso.dto.VerifyOtpDto;
import com.sas.sso.entity.User;
import com.sas.sso.entity.VendorSmsLog;
import com.sas.sso.exception.InternalServerErrorException;
import com.sas.sso.repository.UserRepository;
import com.sas.sso.repository.VendorSmsLogRepository;
import com.sas.sso.request.CreateNewPasswordRequest;
import com.sas.sso.service.ForgetPasswordService;
import com.sas.sso.utils.SmsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Service
@Slf4j
@AllArgsConstructor
public class ForgetPasswordServiceImpl implements ForgetPasswordService, Constant {
    private final UserRepository userRepository;
    private final SmsUtil smsUtil;
    private final SmsProperties smsProperties;
    private final VendorSmsLogRepository vendorSmsLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailProperties emailProperties;

    @Override
    public ModelAndView forgetPassword(ForgetPasswordDto forgetPasswordDto) throws InternalServerErrorException {
        ModelAndView modelAndView = new ModelAndView();
        boolean isValid = true;
        if (!StringUtils.hasText(forgetPasswordDto.getUserName())) {
            log.warn("Email id cannot be empty");
            modelAndView.setViewName("Login_v1/forgetPassword");
            modelAndView.addObject("error_message_user_name", "userName cannot be empty");
            isValid = false;
        }
        if (!StringUtils.hasText(forgetPasswordDto.getCompanyCode())) {
            log.warn("Company code cannot be empty");
            modelAndView.setViewName("Login_v1/forgetPassword");
            modelAndView.addObject("error_message_company_code", "Company code cannot be empty");
            isValid = false;
        }
        if (isValid) {
            String otp = RandomStringUtils.randomNumeric(6);
            String message = "Use OTP " + otp + " to reset your SVCL-FINNCUB password. Do not share the OTP or your number with anyone-SV Creditline Ltd";
            User user = getUser(forgetPasswordDto.getUserName(), forgetPasswordDto.getCompanyCode());
            if (user == null) {
                log.error("No user details found against email id {}", forgetPasswordDto.getUserName());
                ForgetPasswordDto forgetDto = new ForgetPasswordDto();
                modelAndView.setViewName("Login_v1/forgetPassword");
                modelAndView.addObject("forgetPasswordDto", forgetDto);
                modelAndView.addObject("error_message", "No user details found");
            }
            else {
                if(StringUtils.hasText(user.getMobile())) {
                    getVendorSmsLog(otp, message, user);
                }
                VendorSmsLog vendorSmsLogData = new VendorSmsLog();
                ModelAndView sendOtpOnMail = sendOtpOnMail(otp, message, user, vendorSmsLogData);
                if (sendOtpOnMail.getStatus() != null && sendOtpOnMail.getStatus().is2xxSuccessful()) {
                    vendorSmsLogData.setMailStatus("D");  // Status D is for Delivered.
                    vendorSmsLogData.setSmsResponse("otp sent");
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    log.info("Message delivered to user {}", user.getEmail());
                    modelAndView.setViewName("Login_v1/verifyOtp");
                    modelAndView.addObject("verifyOtpDto", new VerifyOtpDto(user.getEmail(), user.getCompanyMaster().getCompanyCode()));
                    modelAndView.addObject("success_message", "Otp sent to the registered email");
                } else {
                    modelAndView.setViewName("Login_v1/forgetPassword");
                    modelAndView.addObject("error_message", "Email not sent");
                }
            }
        }
        return modelAndView;
    }

    private ModelAndView sendOtpOnMail(String otp, String message, User user, VendorSmsLog vendorSmsLogData) {
        vendorSmsLogData.setSmsEmail(user.getEmail());
        vendorSmsLogData.setSmsText(message);
        vendorSmsLogData.setSmsType("FORGET"); // FORGET is for FORGET type
        vendorSmsLogData.setMailStatus("S"); // S is for SENT status
        vendorSmsLogData.setSmsOtp(otp);
        vendorSmsLogData.setSmsVendor("SMTP");
        vendorSmsLogData.setCreatedBy(user.getEmail());
        vendorSmsLogData.setCreatedOn(LocalDateTime.now());
        vendorSmsLogRepository.save(vendorSmsLogData);

        Properties properties = getProperties();
        //Step 1: to get the session object.
        Session session = getSession(properties);
        //Step 2 : compose the message [text, multi media]
        MimeMessage m = new MimeMessage(session);
        MimeMultipart multiPart = new MimeMultipart();
        ModelAndView modelAndView = new ModelAndView();
        try {
            m.setFrom(emailProperties.getSender());
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            m.setSubject(OTP_SUBJECT);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message);
            multiPart.addBodyPart(messageBodyPart);
            //Step 3 : send the message using Transport class
            m.setContent(multiPart);
            Transport.send(m);
            modelAndView.setViewName("Login_v1/forgetPassword");
            modelAndView.setStatus(HttpStatus.OK);
        } catch (Exception exception) {
            log.info("exception occurred due to {}", exception.getMessage());
            ForgetPasswordDto forgetDto = new ForgetPasswordDto();
            modelAndView.setViewName("Login_v1/forgetPassword");
            modelAndView.addObject("forgetPasswordDto", forgetDto);
            modelAndView.addObject("error_message", "Exception occurred due to " + exception.getMessage());
        }
        return modelAndView;
    }

    public Properties getProperties() {
        //get the system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", emailProperties.getHost());
        properties.put("mail.smtp.port", emailProperties.getPort());
        properties.put("mail.smtp.ssl.enable", emailProperties.getEnable());
        properties.put("mail.smtp.auth", emailProperties.getAuth());
        return properties;
    }

    private Session getSession(Properties properties) {
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getSender(), emailProperties.getPassword());
            }
        });
        session.setDebug(true);
        return session;
    }

    @Override
    @Transactional
    public ModelAndView verifyOtp(VerifyOtpDto dto) {
        ModelAndView modelAndView = new ModelAndView();
        if (!StringUtils.hasText(dto.getUserName()) || !StringUtils.hasText(dto.getOtp())) {
            log.error("OTP cannot be empty.");
            modelAndView.addObject("verifyOtpDto", new VerifyOtpDto(dto.getUserName(), dto.getCompanyCode()));
            modelAndView.addObject("error_message", "Otp cannot be empty");
            modelAndView.setViewName("Login_v1/verifyOtp");
            return modelAndView;
        }
        // first check data is available in database
        User user = getUser(dto.getUserName(), dto.getCompanyCode());
        if (user == null) {
            log.error("User details not found for user id {}", dto.getUserName());
            modelAndView.addObject("verifyOtpDto", new VerifyOtpDto(dto.getUserName(), dto.getCompanyCode()));
            modelAndView.addObject("error_message", "User details not found");
            modelAndView.setViewName("Login_v1/verifyOtp");
            return modelAndView;
        }
        Optional<VendorSmsLog> mobileOtp = vendorSmsLogRepository.findTop1BySmsMobileAndStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getMobile(), "D", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        Optional<VendorSmsLog> emailOtp = vendorSmsLogRepository.findTop1BySmsEmailAndMailStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getEmail(), "D", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        // otp check
        if (emailOtp.isPresent() && dto.getOtp().equalsIgnoreCase(emailOtp.get().getSmsOtp())) {
            emailOtp.get().setMailStatus("U");
            emailOtp.get().setModifiedBy(dto.getUserName());
            emailOtp.get().setModifiedOn(LocalDateTime.now());
            vendorSmsLogRepository.save(emailOtp.get());
            user.setIsOtpValidated("Y");
            userRepository.save(user);
            log.info("OTP verified successfully for userId {}", dto.getUserName());
            modelAndView.setViewName("Login_v1/updatePassword");
            modelAndView.addObject("createPasswordDto", new CreateNewPasswordRequest(user.getUsername(), user.getCompanyMaster().getCompanyCode(), dto.getOtp()));
            modelAndView.addObject("success_message", "OTP verified successfully");

        } else if (mobileOtp.isPresent() && dto.getOtp().equalsIgnoreCase(mobileOtp.get().getSmsOtp())) {
            mobileOtp.get().setStatus("U");    // U is for USED status
            mobileOtp.get().setModifiedBy(dto.getUserName());
            mobileOtp.get().setModifiedOn(LocalDateTime.now());
            vendorSmsLogRepository.save(mobileOtp.get());
            user.setIsOtpValidated("Y");
            userRepository.save(user);
            log.info("OTP verified successfully for userId {}", dto.getUserName());
            modelAndView.addObject("createPasswordDto", new CreateNewPasswordRequest(user.getUsername(), user.getCompanyMaster().getCompanyCode(), dto.getOtp()));
            modelAndView.addObject("success_message", "OTP verified successfully");
            modelAndView.setViewName("Login_v1/updatePassword");
        } else {
            log.error("Invalid Otp is entered by the user {}", dto.getUserName());
            modelAndView.addObject("verifyOtpDto", new VerifyOtpDto(dto.getUserName(), dto.getCompanyCode()));
            modelAndView.addObject("error_message", "Invalid Otp is entered by the user");
            modelAndView.setViewName("Login_v1/verifyOtp");
        }
        return modelAndView;
    }

    @Override
    @Transactional
    public ModelAndView updatePassword(CreateNewPasswordRequest createNewPasswordRequest) {
        ModelAndView modelAndView = new ModelAndView();
        if (!createNewPasswordRequest.getNewPassword().equals(createNewPasswordRequest.getConfirmPassword())) {
            log.error("New password is not same as confirm password for userId : {} ", createNewPasswordRequest.getUserName());
            modelAndView.addObject("createPasswordDto", new CreateNewPasswordRequest(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode(), createNewPasswordRequest.getOtp()));
            modelAndView.addObject("error_message", "New password is not same as confirm password");
            modelAndView.setViewName("Login_v1/updatePassword");
        }
        if (createNewPasswordRequest.getNewPassword().length() < 5) {
            log.error("Minimum length of new password should be at least 5 characters");
            modelAndView.addObject("createPasswordDto", new CreateNewPasswordRequest(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode(), createNewPasswordRequest.getOtp()));
            modelAndView.addObject("error_message", "Minimum length of new password should be at least 5 characters");
            modelAndView.setViewName("Login_v1/updatePassword");
        }
        User user = getUser(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode());
        if (user == null) {
            modelAndView.addObject("createPasswordDto", new CreateNewPasswordRequest(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode(), createNewPasswordRequest.getOtp()));
            modelAndView.addObject("error_message", "No user detail found against email id ");
            modelAndView.setViewName("Login_v1/updatePassword");
        }
        Optional<VendorSmsLog> mobileOtp = Optional.empty();
        assert user != null;
        if(StringUtils.hasText(user.getMobile())) {
             mobileOtp = vendorSmsLogRepository.findTop1BySmsMobileAndStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getMobile(), "U", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        }

        Optional<VendorSmsLog> emailOtp = vendorSmsLogRepository.findTop1BySmsEmailAndMailStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(user.getEmail(), "U", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));

        if (emailOtp.isPresent() && createNewPasswordRequest.getOtp().equalsIgnoreCase(emailOtp.get().getSmsOtp())) {
            user.setPassword(passwordEncoder.encode(createNewPasswordRequest.getNewPassword()));
            user.setIsPasswordActive("Y");
            emailOtp.get().setStatus("E");
            vendorSmsLogRepository.save(emailOtp.get());
            user.setUpdatedOn(new Date());
            user.setUpdatedBy(createNewPasswordRequest.getUserName());
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", createNewPasswordRequest.getUserName());
            modelAndView.addObject("loginDTO", new LoginDTO(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode()));
            modelAndView.addObject("success_message", "Password reset was successful");
            modelAndView.setViewName("Login_v1/index");

        } else if (mobileOtp.isPresent() && createNewPasswordRequest.getOtp().equalsIgnoreCase(mobileOtp.get().getSmsOtp())) {
            user.setPassword(passwordEncoder.encode(createNewPasswordRequest.getNewPassword()));
            user.setIsPasswordActive("Y");
            mobileOtp.get().setStatus("E");
            vendorSmsLogRepository.save(mobileOtp.get());
            user.setUpdatedOn(new Date());
            user.setUpdatedBy(createNewPasswordRequest.getUserName());
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", createNewPasswordRequest.getUserName());
            modelAndView.addObject("loginDTO", new LoginDTO(createNewPasswordRequest.getUserName(), createNewPasswordRequest.getCompanyCode()));
            modelAndView.addObject("success_message", "Password reset was successful");
            modelAndView.setViewName("Login_v1/index");
        } else {
            log.error("Otp is not verified");
            modelAndView.addObject("forgetPasswordDto", new ForgetPasswordDto());
            modelAndView.addObject("error_message", "Otp is not verified");
            modelAndView.setViewName("Login_v1/forgetPassword");
        }
        return modelAndView;
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
            log.info("Message delivered to user {}", user.getMobile());
        } else {
            throw new InternalServerErrorException("Empty response received from vendor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User getUser(String email, String companyCode) {
        Optional<User> user = userRepository.findByEmailAndCompanyMaster_CompanyCode(email, companyCode);
        return user.orElse(null);
    }
}
