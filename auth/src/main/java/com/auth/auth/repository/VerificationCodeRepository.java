package com.auth.auth.repository;

import com.auth.auth.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    @Query("SELECT r FROM VerificationCode r WHERE r.verificationCode = ?1")
    Optional<VerificationCode> findByCode(String verificationCode);
}
