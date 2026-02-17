package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.TalentProfile;

import java.util.Set;

public record TalentProfilePatchRequest(
        String title,
        Integer experienceYear,
        String about,
        Set<String> skills
) {
    public void applyTo(TalentProfile profile) {
        if (title != null) profile.setTitle(title);
        if (experienceYear != null) profile.setExperienceYear(experienceYear);
        if (about != null) profile.setAbout(about);
        if (skills != null) profile.setSkills(skills);
    }
}
