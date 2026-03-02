    package com.TalentWorld.backend.dto.request;

    import com.TalentWorld.backend.enums.Role;

    import java.util.Set;

    public record UserFilterRequest(

            String email,
            String name,

            Boolean isActive,

            Set<Role> roles

    ) {
    }