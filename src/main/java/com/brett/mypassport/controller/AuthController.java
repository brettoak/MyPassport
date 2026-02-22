package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.LoginRequest;
import com.brett.mypassport.dto.LoginResponse;
import com.brett.mypassport.dto.RefreshTokenRequest;
import com.brett.mypassport.dto.RegisterRequest;
import com.brett.mypassport.dto.ResetPasswordRequest;
import com.brett.mypassport.dto.VerificationRequest;
import com.brett.mypassport.dto.TokenValidationRequest;
import com.brett.mypassport.service.UserService;
import com.brett.mypassport.service.VerificationService;
import com.brett.mypassport.config.RsaKeyProperties;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = ApiConstants.API_V1 + "/auth", produces = "application/json")
@Tag(name = "Authentication", description = "APIs for user authentication and verification")
@Validated
public class AuthController {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RsaKeyProperties rsaKeyProperties;

    @Operation(summary = "Get Public Key", description = "Returns the RSA Public Key for token verification.")
    @Order(10)
    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {
        RSAPublicKey publicKey = rsaKeyProperties.getPublicKey();
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----";
        return Map.of("algorithm", "RS256", "publicKey", publicKeyPEM);
    }

    @Operation(summary = "Check Token", description = "Validates the token signature and expiration, and checks if it has been revoked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result returned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(9)
    @PostMapping("/check-token")
    public Map<String, Object> checkToken(@Valid @RequestBody TokenValidationRequest request) {
        return userService.validateToken(request.getToken());
    }

    @Operation(summary = "Send Verification Code", description = "Generates a verification code and sends it to the provided email address. The code expires in 60 seconds.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(2)
    @PostMapping("/send-code")
    public com.brett.mypassport.common.ApiResponse<String> sendCode(@Valid @RequestBody VerificationRequest request) {

        verificationService.sendVerificationCode(request.getEmail());
        return com.brett.mypassport.common.ApiResponse.success("Verification code sent successfully.");
    }

    @Operation(summary = "Register User", description = "Registers a new user. Requires a valid verification code sent to the email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or verification code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(3)
    @PostMapping("/register")
    public com.brett.mypassport.common.ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return com.brett.mypassport.common.ApiResponse.success("User registered successfully.");
    }

    @Operation(summary = "Login User", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(1)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        String deviceInfo = httpRequest.getHeader("User-Agent");
        return userService.login(request, ipAddress, deviceInfo);
    }

    @Operation(summary = "Refresh Token", description = "Generates a new access token and refresh token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refresh successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(6)
    @PostMapping("/refresh-token")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return userService.refreshToken(request);
    }

    @Operation(summary = "Logout", description = "Invalidates the current access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(7)
    @PostMapping("/logout")
    public com.brett.mypassport.common.ApiResponse<String> logout(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token is missing or invalid");
        }

        String token = authHeader.substring(7);
        userService.logout(token);
        return com.brett.mypassport.common.ApiResponse.success("Logout successful");
    }

    @Operation(summary = "Logout All Devices", description = "Invalidates all active tokens for the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All devices logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(8)
    @PostMapping("/logout-all")
    public com.brett.mypassport.common.ApiResponse<String> logoutAll(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token is missing or invalid");
        }

        String token = authHeader.substring(7);
        userService.logoutAll(token);
        return com.brett.mypassport.common.ApiResponse.success("All devices logged out successfully");
    }

    @Operation(summary = "Forgot Password", description = "Sends a verification code to the user's email if the account exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(4)
    @PostMapping("/forgot-password")
    public com.brett.mypassport.common.ApiResponse<String> forgotPassword(@Valid @RequestBody VerificationRequest request) {

        userService.requestPasswordReset(request.getEmail());
        return com.brett.mypassport.common.ApiResponse.success("Verification code sent successfully.");
    }

    @Operation(summary = "Reset Password", description = "Resets the user's password using a valid verification code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid code or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(5)
    @PostMapping("/reset-password")
    public com.brett.mypassport.common.ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return com.brett.mypassport.common.ApiResponse.success("Password reset successfully.");
    }
}
