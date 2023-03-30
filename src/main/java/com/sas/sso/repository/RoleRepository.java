package com.sas.sso.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sas.sso.entity.Role;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Role findByRole(String role);

	@Query(nativeQuery = true, value = "SELECT role FROM role r INNER JOIN access_group_role agr ON agr.role_id=r.role_id INNER JOIN user_access_group uag ON uag.access_group_id=agr.access_group_id WHERE uag.user_id=?1")
	Set<String> findAllRolesForUser(Long userId);

	Optional<Role> findById(Long id);

	@Query(nativeQuery = true, value = "SELECT DISTINCT(role_group) FROM role r")
	Set<String> findAllRolesGroups();
	
	Set<Role> findByRoleGroup(String roleGroup);

}
