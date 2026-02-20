package com.TalentWorld.backend.dto.request;

import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record JobPostCreateRequest(

        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 2000)
        String description,

        String location,

        @NotNull
        WorkType workType,

        @NotNull
        EmploymentType employmentType,

        @NotNull
        ExperienceLevel experienceLevel,

        Integer minExperienceYear,
        Integer maxExperienceYear,

        Set<String> skills

){
    public static JobPost toEntityDto(JobPostCreateRequest dto) {
        JobPost jobPost=new JobPost();
        jobPost.setTitle(dto.title);
        jobPost.setDescription(dto.description);
        jobPost.setLocation(dto.location);
        jobPost.setWorkType(dto.workType);
        jobPost.setEmploymentType(dto.employmentType);
        jobPost.setExperienceLevel(dto.experienceLevel);
        jobPost.setMinExperienceYear(dto.minExperienceYear);
        jobPost.setMaxExperienceYear(dto.maxExperienceYear);
        jobPost.setSkills(dto.skills);
        return jobPost;
    }
}

