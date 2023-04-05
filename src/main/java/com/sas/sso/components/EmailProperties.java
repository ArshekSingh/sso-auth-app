package com.sas.sso.components;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class EmailProperties {

    @Value("${spring.recipient.email}")
    private String recipient;

    @Value("${spring.mail.from}")
    private String sender;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.smtp.host}")
    private String host;

    @Value("${spring.mail.smtp.port}")
    private String port;

    @Value("${spring.mail.smtp.ssl.enable}")
    private String enable;

    @Value("${spring.mail.smtp.auth}")
    private String auth;

    @Value("${spring.recipient.cc.email}")
    private String cc;

    @Value("${spring.recipient.bcc.email}")
    private String bcc;
}
