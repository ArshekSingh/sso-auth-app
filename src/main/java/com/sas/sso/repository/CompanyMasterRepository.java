package com.sas.sso.repository;

import com.sas.sso.entity.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMasterRepository extends JpaRepository<CompanyMaster, Long> {


    Optional<List<CompanyMaster>> findByActive(Boolean active);
}
