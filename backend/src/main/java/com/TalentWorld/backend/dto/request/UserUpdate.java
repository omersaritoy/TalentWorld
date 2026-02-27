package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UserUpdate(String firstName, String lastName) {


    public void applyTo(User user) {
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
    }
}
