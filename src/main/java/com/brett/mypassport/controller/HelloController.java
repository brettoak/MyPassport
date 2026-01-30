package com.brett.mypassport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Operation(summary = "Get hello message", description = "Returns a JSON object containing a greeting message and status.")
    @GetMapping("/hello")
    public String hello() {
        return """
                {
                  "message": "Hello, World!",
                  "status": "success"
                }
                """;
    }
}
