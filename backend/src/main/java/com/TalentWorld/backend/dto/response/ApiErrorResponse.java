package com.TalentWorld.backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final int status;
    private final String path;
    private final Instant timestamp;
    private final Map<String, Object> details;

    public ApiErrorResponse(String code, String message, int status, String path, Instant timestamp, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
        this.details = details;
    }

}
