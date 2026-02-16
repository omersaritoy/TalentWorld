package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.TalentProfile;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import java.util.Set;

public record UserResponse(String id, String firstName, String lastName, String email, Boolean isActive,
                           Set<Role> roles) {

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getIsActive(), user.getRoles());
    }
}
