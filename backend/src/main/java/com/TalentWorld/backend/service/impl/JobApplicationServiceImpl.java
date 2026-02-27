package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.*;
import com.TalentWorld.backend.dto.response.*;
import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.ApplicationStatus;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.*;
import com.TalentWorld.backend.repository.*;
import com.TalentWorld.backend.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.TalentWorld.backend.enums.Role.ROLE_ADMIN;
import static com.TalentWorld.backend.enums.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    public JobApplicationResponse applyToJob(
            String jobPostId,
            User talent,
            JobApplicationCreateRequest request
    ) {

        if (!talent.getRoles().contains(ROLE_USER)) {
            throw new BusinessException(
                    "Only talent users can apply",
                    "ROLE_NOT_ALLOWED",
                    HttpStatus.FORBIDDEN
            );
        }

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new BusinessException(
                        "Job post not found",
                        "JOB_POST_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));


        if (!jobPost.getIsActive()) {
            throw new BusinessException(
                    "Job post is not active",
                    "JOB_POST_INACTIVE",
                    HttpStatus.BAD_REQUEST
            );
        }


        if (jobApplicationRepository.existsByJobPostAndTalent(jobPost, talent)) {
            throw new BusinessException(
                    "You already applied to this job",
                    "ALREADY_APPLIED",
                    HttpStatus.CONFLICT
            );
        }

        JobApplication application = new JobApplication();
        application.setJobPost(jobPost);
        application.setTalent(talent);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter(request.coverLetter());
        application.setAppliedAt(LocalDateTime.now());

        return JobApplicationResponse.toDto(
                jobApplicationRepository.save(application)
        );
    }
    @Override
    @Transactional()
    public List<JobApplicationResponse> getApplicationsForJob(
            String jobPostId,
            User recruiter
    ) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new BusinessException(
                        "Job post not found",
                        "JOB_POST_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));


        boolean isOwner = jobPost.getUser().getId().equals(recruiter.getId());
        boolean isAdmin = recruiter.getRoles().contains(ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            throw new BusinessException(
                    "You are not allowed to view applications",
                    "ACCESS_DENIED",
                    HttpStatus.FORBIDDEN
            );
        }

        return jobApplicationRepository.findByJobPost(jobPost)
                .stream()
                .map(JobApplicationResponse::toDto)
                .toList();
    }

    @Override
    @Transactional()
    public List<MyApplicationResponse> getMyApplications(User talent) {

        if (!talent.getRoles().contains(ROLE_USER)) {
            throw new BusinessException(
                    "Only talent users can view their applications",
                    "ROLE_NOT_ALLOWED",
                    HttpStatus.FORBIDDEN
            );
        }

        return jobApplicationRepository.findByTalent(talent)
                .stream()
                .map(MyApplicationResponse::toDto)
                .toList();
    }
    @Override
    public JobApplicationResponse updateApplicationStatus(
            String applicationId,
            User recruiter,
            JobApplicationStatusUpdateRequest request
    ) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(
                        "Application not found",
                        "APPLICATION_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        JobPost jobPost = application.getJobPost();

        boolean isOwner = jobPost.getUser().getId().equals(recruiter.getId());
        boolean isAdmin = recruiter.getRoles().contains(ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            throw new BusinessException(
                    "You are not allowed to update this application",
                    "ACCESS_DENIED",
                    HttpStatus.FORBIDDEN
            );
        }

        application.setStatus(request.status());

        return JobApplicationResponse.toDto(application);
    }

}