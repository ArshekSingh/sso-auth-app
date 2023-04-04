package com.sas.sso.entity;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

	@CreatedBy
	@Column(name = "created_by")
	protected U createdBy;

	@CreatedDate
	@Temporal(TIMESTAMP)
	@Column(name = "created_on")
	protected Date createdDate;

	@LastModifiedBy
	@Column(name = "updated_by")
	protected U updatedBy;

	@LastModifiedDate
	@Temporal(TIMESTAMP)
	@Column(name = "updated_on")
	protected Date updatedOn;

}
