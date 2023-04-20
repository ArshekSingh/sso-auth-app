package com.sas.sso.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "company_master")
public class CompanyMaster extends Auditable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comp_id")
    private Long companyId;

    @Column(name = "comp_name")
    private String companyName;

    @Column(name = "comp_code", unique = true)
    private String companyCode;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "active")
    private boolean active;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "secret_id")
    private String secretId;

}
