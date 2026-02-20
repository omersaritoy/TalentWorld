package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.WorkType;

import java.util.Set;

public record JobPostUpdateRequest(

        String title,
        String description,
        String location,

        WorkType workType,

        EmploymentType employmentType,
        ExperienceLevel experienceLevel,

        Integer minExperienceYear,
        Integer maxExperienceYear,

        Set<String> skills,
        Boolean isActive

) {
    public void applyTo(JobPost jobPost) {

        if (title != null) jobPost.setTitle(title);
        if (description != null) jobPost.setDescription(description);
        if (location != null) jobPost.setLocation(location);

        if (workType != null) jobPost.setWorkType(workType);

        if (employmentType != null) jobPost.setEmploymentType(employmentType);
        if (experienceLevel != null) jobPost.setExperienceLevel(experienceLevel);

        if (minExperienceYear != null) jobPost.setMinExperienceYear(minExperienceYear);
        if (maxExperienceYear != null) jobPost.setMaxExperienceYear(maxExperienceYear);

        if (skills != null) jobPost.setSkills(skills);
        if (isActive != null) jobPost.setIsActive(isActive);
    }
}
