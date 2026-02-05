package com.brett.mypassport.service;

import com.brett.mypassport.dto.LoginRequest;
import com.brett.mypassport.dto.LoginResponse;
import com.brett.mypassport.dto.RegisterRequest;
import com.brett.mypassport.entity.User;
import com.brett.mypassport.common.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // 1. Find user by email (or username, logic can be added)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // 2. Verify password (simple check for now, should be hashed)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // 3. Generate Token
        String token = jwtUtil.generateToken(user.getUsername());

        // 4. Return Response
        return new LoginResponse(user.getUsername(), user.getEmail(), token);
    }

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
