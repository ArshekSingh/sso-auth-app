package com.sas.sso.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sas.sso.entity.AccessGroup;

@Transactional
@Repository
public interface AccessGroupRepository extends PagingAndSortingRepository<AccessGroup, Integer> {

	List<AccessGroup> findTopByOrderByCreatedDateDesc();

	List<AccessGroup> findAllByOrderByCreatedDateDesc();

	List<AccessGroup> findByActive(int active);

	Page<AccessGroup> findAll(Pageable pageable);

	boolean existsByName(String name);

	Optional<AccessGroup> findById(Long id);

	@Query(value = "SELECT agr.role_name FROM access_group ag "
			+ "inner join access_group_role agr on agr.access_group_id=ag.id "
			+ "inner join company_master cm on cm.comp_id=ag.comp_id "
			+ "inner join user_access_group uag on uag.access_group_id=ag.id "
			+ "inner join app_master am on am.comp_id = cm.comp_id "
			+ "where uag.user_id=?1  and am.app_name=?2", nativeQuery = true)
	Set<String> findAllRolesOfAppAndCompany(Long userId,String appName);
}
