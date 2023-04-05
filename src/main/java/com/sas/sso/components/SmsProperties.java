package com.sas.sso.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@PropertySource("classpath:application-sms-${spring.profiles.active}.properties")
public class SmsProperties {

    @Value("${sms_url}")
    private String smsUrl;

    @Value("${user_name}")
    private String username;

    @Value("${pass}")
    private String pass;

    @Value("${senderid}")
    private String senderid;

    @Value("${msgtype}")
    private String msgtype;

    @Value("${response}")
    private String response;

    @Value("${otpExpiryTime}")
    private Long otpExpiryTime;

    @Value("${otpExpiryCron}")
    private String otpExpiryCron;
}