package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.User;

public record UserResponse(String id, String firstName, String lastName, String email,Boolean isActive) {
    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getIsActive());
    }
}
