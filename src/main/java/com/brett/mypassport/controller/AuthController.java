package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.VerificationRequest;
import com.brett.mypassport.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/auth")
public class AuthController {

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody VerificationRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email cannot be empty");
        }

        try {
            verificationService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok("Verification code sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send code: " + e.getMessage());
        }
    }
}
