package com.brett.mypassport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@Validated
public class HelloController {

    @Operation(summary = "Echo message", description = "Returns the input message if it is within 20 characters.")
    @GetMapping("/hello")
    public String hello(@RequestParam @Size(max = 20) String message) {

        return """
                {
                  "message": "your message is: %s",
                  "status": "success"
                }
                """.formatted(message);
    }

}