package com.TalentWorld.backend.dto.request;

public record SignInRequest(
        String email,
        String password
) {}