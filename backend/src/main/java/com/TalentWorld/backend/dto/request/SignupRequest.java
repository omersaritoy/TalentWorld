package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignupRequest(

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Please enter a valid email address.")
        @Size(max = 100, message = "Email cannot be more 100 character")
        String email,
        @NotBlank(message = "Password must not be empty")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,
        Set<Role> roles
) {

    public static User toUser(SignupRequest signupRequest) {
        return new User(signupRequest.firstName, signupRequest.lastName, signupRequest.email, signupRequest.password, signupRequest.roles);
    }

}
