package com.TalentWorld.backend.config;

import com.TalentWorld.backend.dto.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class AccessDeniedHandlerConfig implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public AccessDeniedHandlerConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse apiError = new ApiErrorResponse(
                "FORBIDDEN",
                "Authorization Denied",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI(),
                Instant.now(),
                Map.of("error", "Unauthorized")
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }

}
