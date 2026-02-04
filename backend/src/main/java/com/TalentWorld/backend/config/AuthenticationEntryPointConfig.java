package com.TalentWorld.backend.config;


import com.TalentWorld.backend.dto.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class AuthenticationEntryPointConfig implements AuthenticationEntryPoint {
    private final ObjectMapper mapper;

    public AuthenticationEntryPointConfig(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ApiErrorResponse apiError = new ApiErrorResponse(
                "UNAUTHORIZED",
                "Authentication fail: " + authException.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(),
                Instant.now(),
                Map.of("error", "Authentication fail")
        );

        response.getWriter().println(mapper.writeValueAsString(apiError));
    }
}
