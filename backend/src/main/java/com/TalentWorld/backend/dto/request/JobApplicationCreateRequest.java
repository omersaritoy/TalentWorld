package com.TalentWorld.backend.dto.request;

import jakarta.validation.constraints.Size;

public record JobApplicationCreateRequest(

        @Size(max = 1000)
        String coverLetter

) {
}
