package com.brett.mypassport.service;

import com.brett.mypassport.dto.RegisterRequest;
import com.brett.mypassport.entity.User;
import com.brett.mypassport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Transactional
    public void registerUser(RegisterRequest request) {
        // 1. Verify code
        boolean isVerified = verificationService.verifyCode(request.getEmail(), request.getVerificationCode());
        if (!isVerified) {
            throw new IllegalArgumentException("Invalid or expired verification code.");
        }

        // 2. Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // 3. Create and save user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In a real app, hash this!

        userRepository.save(user);
    }
}
