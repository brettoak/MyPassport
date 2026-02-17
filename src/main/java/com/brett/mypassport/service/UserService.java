package com.brett.mypassport.service;

import java.util.List;
import com.brett.mypassport.dto.LoginRequest;
import com.brett.mypassport.dto.LoginResponse;
import com.brett.mypassport.dto.LoginResponse;
import com.brett.mypassport.dto.RefreshTokenRequest;
import com.brett.mypassport.dto.RegisterRequest;
import com.brett.mypassport.dto.ResetPasswordRequest;
import com.brett.mypassport.dto.UserResponse;
import com.brett.mypassport.entity.Token;
import com.brett.mypassport.entity.User;
import com.brett.mypassport.common.JwtUtil;
import com.brett.mypassport.repository.TokenRepository;
import com.brett.mypassport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 1. Find user by email (or username, logic can be added)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // 2. Verify password (simple check for now, should be hashed)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // 3. Generate Tokens
        String jwtToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 4. Save Token
        saveUserToken(user, jwtToken, refreshToken);

        // 5. Return Response
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                jwtToken,
                jwtUtil.getExpirationTime(),
                refreshToken,
                jwtUtil.getRefreshTokenExpirationTime());
    }

    private void saveUserToken(User user, String jwtToken, String refreshToken) {
        // Option: Revoke old tokens here if needed
        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setRefreshToken(refreshToken);
        token.setTokenType("BEARER");
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // 1. Find token in DB
        Token token = tokenRepository.findByRefreshToken(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        // 2. Check if revoked or expired in DB
        if (token.isRevoked() || token.isExpired()) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        // 3. Verify signature and expiration (JWT level)
        String username = token.getUser().getUsername();
        if (!jwtUtil.validateToken(requestRefreshToken, username)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 4. Revoke old token
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);

        // 5. Generate new tokens
        User user = token.getUser();
        String newJwtToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 6. Save new token
        saveUserToken(user, newJwtToken, newRefreshToken);

        // 7. Return Response
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                newJwtToken,
                jwtUtil.getExpirationTime(),
                newRefreshToken,
                jwtUtil.getRefreshTokenExpirationTime());
    }

    @Transactional
    public void logout(String tokenValue) {
        Token token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        if (token.isExpired() || token.isRevoked()) {
            throw new IllegalArgumentException("Token is already invalid");
        }

        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }

    @Transactional
    public void logoutAll(String tokenValue) {
        // Ensure the initiating token is valid
        Token initiatingToken = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        if (initiatingToken.isExpired() || initiatingToken.isRevoked()) {
            throw new IllegalArgumentException("Token is already invalid");
        }

        // Extract username from token
        String username = jwtUtil.extractUsername(tokenValue);

        // Ensure user exists
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Revoke all tokens
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // No roles for now
        );
    }

    public UserResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


    public void requestPasswordReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            // To prevent user enumeration, we might want to return success even if email doesn't exist.
            // However, for this implementation, we'll throw an exception or handle it.
            // Let's go with throwing exception for now for simplicity, or just return.
            // Better security practice: do not reveal if email exists.
            // But if we return, the user won't get an email.
            // Let's throw exception for now as it's an internal API mostly.
            throw new IllegalArgumentException("User with this email does not exist.");
        }
        verificationService.sendVerificationCode(email);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Verify code
        boolean isVerified = verificationService.verifyCode(request.getEmail(), request.getVerificationCode());
        if (!isVerified) {
            throw new IllegalArgumentException("Invalid or expired verification code.");
        }

        // 2. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 3. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 4. Invalidate code
        verificationService.deleteCode(request.getEmail());

        // 5. Optionally revoke all tokens (security best practice)
        // revokeAllUserTokens(user); // If we had this method exposed/implemented easily here
    }
}
