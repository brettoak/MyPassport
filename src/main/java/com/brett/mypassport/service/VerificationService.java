package com.brett.mypassport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;

    private static final String KEY_PREFIX = "verification_code:";

    public void sendVerificationCode(String email) {
        String code = generateCode();
        // Save to Redis with 60s expiration
        redisTemplate.opsForValue().set(KEY_PREFIX + email, code, 60, TimeUnit.SECONDS);

        // Send email
        String subject = "Your Verification Code";
        String content = "Your verification code is: " + code + "\nThis code will expire in 60 seconds.";
        emailService.sendSimpleMessage(email, subject, content);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        return storedCode != null && storedCode.equals(code);
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
