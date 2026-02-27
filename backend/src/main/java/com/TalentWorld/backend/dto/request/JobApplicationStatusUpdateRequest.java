package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record JobApplicationStatusUpdateRequest(

        @NotNull
        ApplicationStatus status

) {
}
