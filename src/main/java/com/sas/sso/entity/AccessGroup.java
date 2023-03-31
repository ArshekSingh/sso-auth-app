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
@Table(name = "access_groups")
public class AccessGroup extends Auditable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "access_group_id")
	private Long id;

	@Column(name = "access_group_no")
	private String accessGroupNo;

	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "active")
	private boolean active;

	@Column(name = "editable")
	private Integer editable;

}
