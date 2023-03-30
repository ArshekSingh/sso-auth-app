package com.sas.sso.repository;

import java.util.List;
import java.util.Optional;

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

	@Query(value = "SELECT ag.* FROM access_groups ag WHERE ag.access_group_id NOT IN (SELECT user_id FROM user_access_group uag WHERE uag.user_id=?1)", nativeQuery = true)
	List<AccessGroup> findAllUnAssignedGroupByUser(Long userId);
}
