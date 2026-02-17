package com.brett.mypassport.config;

import com.brett.mypassport.common.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.brett.mypassport.controller")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all responses from our controllers
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // If already an ApiResponse, return as is
        if (body instanceof ApiResponse) {
            return body;
        }

        // If body is String, we need to manually serialize it because StringHttpMessageConverter
        // will try to cast the result to String, causing ClassCastException if we return ApiResponse
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (JsonProcessingException e) {
                return ApiResponse.error(500, "Internal Serialization Error");
            }
        }
        
        // Wrap everything else in success response
        return ApiResponse.success(body);
    }
}
