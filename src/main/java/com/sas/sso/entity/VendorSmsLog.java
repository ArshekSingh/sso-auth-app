package com.sas.sso.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "VENDOR_SMS_LOG")
public class VendorSmsLog implements Serializable {

    private static final long serialVersionUID = -2338626292552177485L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SMS_ID")
    private Integer smsId;
    @Column(name = "ORG_ID")
    private Long orgId;
    @Column(name = "SMS_MOBILE")
    private String smsMobile;
    @Column(name = "SMS_OTP")
    private String smsOtp;
    @Column(name = "SMS_TEXT")
    private String smsText;
    @Column(name = "SMS_TYPE")
    private String smsType;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "SMS_VENDOR")
    private String smsVendor;
    @Column(name = "SMS_RESPONSE_ID")
    private String smsResponse;
    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;
    @Column(name = "MODIFIED_BY")
    protected String modifiedBy;
    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;
}