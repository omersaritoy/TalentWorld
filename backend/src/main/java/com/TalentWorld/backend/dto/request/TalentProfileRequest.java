package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.TalentProfile;

import java.util.List;
import java.util.Set;

public record TalentProfileRequest(
        String title,
        Integer experienceYear,
        String about,
        Set<String> skills
) {
    public void applyTo(TalentProfile profile) {
        profile.setTitle(title);
        profile.setExperienceYear(experienceYear);
        profile.setAbout(about);
        profile.setSkills(skills);
    }
}

