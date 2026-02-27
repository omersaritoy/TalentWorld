package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.TalentProfile;
import com.TalentWorld.backend.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record TalentProfileResponse(
        String id,
        String title,
        Integer experienceYear,
        String about,
        Set<String> skills
) {
    public static TalentProfileResponse toDto(TalentProfile profile) {
        return new TalentProfileResponse(
                profile.getId(),
                profile.getTitle(),
                profile.getExperienceYear(),
                profile.getAbout(),
                profile.getSkills()
        );
    }
}
