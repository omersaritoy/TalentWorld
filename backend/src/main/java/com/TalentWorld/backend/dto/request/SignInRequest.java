package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.TalentProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record SignInRequest(
        String email,
        String password
) {
}

