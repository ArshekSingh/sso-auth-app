package com.sas.sso.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
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

}
