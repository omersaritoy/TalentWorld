package com.TalentWorld.backend.dto.response;

import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record MyApplicationResponse(

        String applicationId,

        String jobPostId,
        String jobPostTitle,

        ApplicationStatus status,

        LocalDateTime appliedAt

) {
    public static MyApplicationResponse toDto(JobApplication application) {
        return new MyApplicationResponse(
                application.getId(),
                application.getJobPost().getId(),
                application.getJobPost().getTitle(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
