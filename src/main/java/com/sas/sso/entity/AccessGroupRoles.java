package com.sas.sso.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "access_group_role")
public class AccessGroupRoles extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "role_name")
	private String role;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "access_group_id")
	private AccessGroup accessGroup;

}
