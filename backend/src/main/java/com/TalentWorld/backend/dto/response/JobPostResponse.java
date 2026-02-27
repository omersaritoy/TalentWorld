package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.enums.ApplicationStatus;
import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.WorkType;

import java.time.LocalDateTime;
import java.util.Set;

public record JobPostResponse(

        String id,

        String title,
        String description,
        String location,

        WorkType workType,

        EmploymentType employmentType,
        ExperienceLevel experienceLevel,

        Integer minExperienceYear,
        Integer maxExperienceYear,

        Set<String> skills,

        Boolean isActive,
        String recruiterId

) {
    public static JobPostResponse toDto(JobPost jobPost) {
        return new JobPostResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getDescription(),
                jobPost.getLocation(),
                jobPost.getWorkType(),
                jobPost.getEmploymentType(),
                jobPost.getExperienceLevel(),
                jobPost.getMinExperienceYear(),
                jobPost.getMaxExperienceYear(),
                jobPost.getSkills(),
                jobPost.getIsActive(),
                jobPost.getUser().getId()
        );
    }
}
