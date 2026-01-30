package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

public record UserResponse(String id, String firstName, String lastName, String email, Boolean isActive, Role role) {

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getIsActive(),user.getRole());
    }
}
