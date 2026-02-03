package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import java.util.Set;


public record UserUpdateRequest(String firstName, String lastName, String email, Boolean isActive, Set<Role> roles, String password) {

    public static User toUser(UserUpdateRequest request) {
        return new User(request.firstName, request.lastName, request.email,request.isActive,request.roles,request.password);
    }
}
