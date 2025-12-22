package com.example.backend.repository;

import com.example.backend.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IEmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmailAndOtp(String email, String otp);

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}
