package com.example.backend.service.implement;

import com.example.backend.entity.EmailOtp;
import com.example.backend.entity.User;
import com.example.backend.producer.EmailProducer;
import com.example.backend.repository.IEmailOtpRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IEmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailOtpService implements IEmailOtpService {

    private final IEmailOtpRepository _emailOtpRepository;
    private final IUserRepository _userRepository;
    private final EmailProducer emailProducer;

    @Transactional
    public void sendOtp(String email) {
        User user = _userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        _emailOtpRepository.deleteByEmail(email);

        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        _emailOtpRepository.save(emailOtp);

        emailProducer.sendEmailOtp(user.getUserId(), otp);
    }

    public EmailOtp validateOtp(String email, String otp) {
        EmailOtp emailOtp = _emailOtpRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("OTP không hợp lệ"));

        if (emailOtp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        return emailOtp;
    }
}

