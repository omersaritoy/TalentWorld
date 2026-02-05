package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import java.util.Set;
import java.util.stream.Collectors;

public record AuthResponse(
        String token,
        String username,
        Set<String> roles
) {
    public static AuthResponse from(User user, String token) {
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}