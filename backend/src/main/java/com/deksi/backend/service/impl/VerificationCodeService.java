package com.deksi.backend.service.impl;

import com.deksi.backend.model.VerificationCode;
import com.deksi.backend.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    public boolean verifyVerificationCode(String email, String verificationCode) {
        VerificationCode code = verificationCodeRepository.findByEmailAndCode(email, verificationCode);
        return code != null;
    }

    public VerificationCode getVerificationCodeByEmail(String email) {
        return verificationCodeRepository.findByEmail(email);
    }

    public void saveVerificationCode(String email, String code) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCodeRepository.save(verificationCode);
    }
}
