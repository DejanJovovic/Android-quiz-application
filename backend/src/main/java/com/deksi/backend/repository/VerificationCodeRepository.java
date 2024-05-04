package com.deksi.backend.repository;

import com.deksi.backend.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    VerificationCode findByEmailAndCode(String email, String code);

    VerificationCode findByEmail(String email);
}