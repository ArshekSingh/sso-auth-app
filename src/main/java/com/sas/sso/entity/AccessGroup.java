package com.sas.sso.entity;

import javax.persistence.*;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "access_groups")
public class AccessGroup extends Auditable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "access_group_id")
	private Long id;

	@Column(name="comp_id")
	private Long copmId;

	@Column(name="app_id")
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

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "access_group_role", joinColumns = @JoinColumn(name = "access_group_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

}
