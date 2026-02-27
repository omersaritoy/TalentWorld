package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record JobApplicationResponse(

        String id,

        String jobPostId,
        String jobPostTitle,

        String talentId,
        String talentEmail,

        ApplicationStatus status,

        String coverLetter,

        LocalDateTime appliedAt

) {
    public static JobApplicationResponse toDto(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getJobPost().getId(),
                application.getJobPost().getTitle(),
                application.getTalent().getId(),
                application.getTalent().getEmail(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getAppliedAt()
        );
    }
}
