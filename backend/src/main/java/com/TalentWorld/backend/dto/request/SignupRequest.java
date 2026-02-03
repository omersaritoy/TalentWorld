package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import java.util.Set;

public record SignupRequest(

        String firstName,
        String lastName,
        String email,
        String password,
        Set<Role> roles
) {

    public static User toUser(SignupRequest signupRequest) {
        return new User(signupRequest.firstName, signupRequest.lastName, signupRequest.email, signupRequest.password, signupRequest.roles);
    }

}
