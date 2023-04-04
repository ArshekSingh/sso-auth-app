package com.sas.sso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sas.sso.entity.AccessGroupRoles;

@Transactional
@Repository
public interface AccessGroupRoleRepository extends PagingAndSortingRepository<AccessGroupRoles, Long> {
}