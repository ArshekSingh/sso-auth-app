package com.sas.sso.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sas.sso.entity.AppMaster;

@Repository
public interface AppMasterRepository extends JpaRepository<AppMaster, Long> {
	Optional<AppMaster> findByApplicationNameAndCompanyId(String applicationName, Long companyId);
	
	Optional<List<AppMaster>> findByCompanyId(Long companyId);
	
	Optional<List<AppMaster>> findByCompanyIdAndActive(Long companyId,Boolean active);
}
