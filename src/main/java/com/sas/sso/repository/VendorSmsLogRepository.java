package com.sas.sso.repository;

import com.sas.sso.entity.VendorSmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VendorSmsLogRepository extends JpaRepository<VendorSmsLog, Long> {

    Optional<VendorSmsLog> findTop1BySmsMobileAndStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(String mobile, String status, String smsType, LocalDateTime expiryTime);
    Optional<VendorSmsLog> findTop1BySmsEmailAndMailStatusAndSmsTypeAndCreatedOnGreaterThanOrderBySmsIdDesc(String email, String mailStatus, String smsType, LocalDateTime expiryTime);
}
