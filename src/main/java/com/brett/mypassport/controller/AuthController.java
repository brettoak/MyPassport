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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and verification")
public class AuthController {

    @Autowired
    private VerificationService verificationService;

    @Operation(summary = "Send Verification Code", description = "Generates a verification code and sends it to the provided email address. The code expires in 60 seconds.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
