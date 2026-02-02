package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import java.util.Set;

public record UserResponse(String id, String firstName, String lastName, String email, Boolean isActive, java.util.Set<Role> roles) {

    public static UserResponse toDto(User user) {
        Set<Role> roles = user.getRoles();
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getIsActive(),user.getRoles());
    }
}
