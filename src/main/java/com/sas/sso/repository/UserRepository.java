package com.sas.sso.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sas.sso.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsById(Long id);

	Optional<User> findById(Long id);

	/*
	 * @Query(nativeQuery = true, value =
	 * "SELECT um.* FROM user_master um inner join company_master cm on cm.comp_id=um.comp_id where cm.company_code=?2 and um.email=?1"
	 * ) Optional<User> findByEmailAndCompanyCompanyCode(String email, String
	 * companyCode);
	 */	
	Optional<User> findByEmailAndCompanyMaster_CompanyCode(String email, String companyCode);

	interface UserAuthView {
		String getUserId();

		String getCompanyId();

		String getCompanyCode();

		String getEmail();

		String getPassword();

		boolean getActive();
	}
}
