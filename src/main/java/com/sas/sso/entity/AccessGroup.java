package com.sas.sso.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "access_group")
public class AccessGroup extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "comp_id")
	private Long compId;

	@Column(name = "app_id")
	private Long appId;

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

	@OneToMany(mappedBy ="accessGroup",cascade = CascadeType.ALL,fetch=FetchType.LAZY,orphanRemoval=true)
	private List<AccessGroupRoles> accessGroupRoles=new ArrayList();
	
}
