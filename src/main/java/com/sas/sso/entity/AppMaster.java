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
@Table(name = "app_master")
public class AppMaster extends Auditable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "comp_id")
	private Long companyId;

	@Column(name = "app_name", unique = true)
	private String applicationName;

	@Column(name = "base_url")
	private String baseUrl;

	@Column(name = "active")
	private boolean active;

}
