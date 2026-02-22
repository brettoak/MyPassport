package com.brett.mypassport.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Validated
@Tag(name = "Hello", description = "APIs for greeting messages")
public class HelloController {

    @Operation(summary = "Echo message", description = "Returns the input message if it is within 20 characters.")
    @Order(999) // Ensure this controller is loaded after all other controllers
    @GetMapping("/hello")
    public java.util.Map<String, String> hello(@RequestParam(required = false, defaultValue = "guest") @Size(max = 20) String message) {
        return java.util.Map.of(
            "message", "your message is: " + message,
            "status", "success"
        );
    }

}