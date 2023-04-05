package com.sas.sso.utils;

import com.sas.sso.components.SmsProperties;
import com.sas.sso.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class SmsUtil {

    private final SmsProperties smsProperties;

    private final RestTemplate restTemplate;

    @Autowired
    public SmsUtil(SmsProperties smsProperties, RestTemplate restTemplate) {
        this.smsProperties = smsProperties;
        this.restTemplate = restTemplate;
    }

    public String sendSms(String mobileNumber, String message) throws InternalServerErrorException {
        log.info("Request initiated to call sms api for mobile number {}", mobileNumber);
        String strUrlPin = smsProperties.getSmsUrl();
        String strUserNamePin = smsProperties.getUsername();
        String strPasswordPin = smsProperties.getPass();
        String strsenderidPin = smsProperties.getSenderid();
        String msgtypePin = smsProperties.getMsgtype();
        String responsePin = smsProperties.getResponse();
        String response;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(strUrlPin).queryParam("username", strUserNamePin).queryParam("pass", strPasswordPin).queryParam("senderid", strsenderidPin).queryParam("dest_mobileno", mobileNumber).queryParam("msgtype", msgtypePin).queryParam("message", message).queryParam("response", responsePin);
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            HttpEntity<String> smsResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
            response = smsResponse.getBody();
            log.info("Response from sms service {}", response);
        } catch (Exception exception) {
            log.error("Exception Occurs While Sending SMS {}", exception.getMessage());
            throw new InternalServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
