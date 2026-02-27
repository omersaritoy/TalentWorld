package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.MyApplicationResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @PostMapping("/job-posts/{jobPostId}/apply")
    @PreAuthorize("hasRole('TALENT')")
    public ResponseEntity<JobApplicationResponse> applyToJob(
            @PathVariable String jobPostId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JobApplicationCreateRequest request
    ) {
        return ResponseEntity.ok(
                jobApplicationService.applyToJob(jobPostId, user, request)
        );
    }
    @GetMapping("/job-posts/{jobPostId}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsForJob(
            @PathVariable String jobPostId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                jobApplicationService.getApplicationsForJob(jobPostId, user)
        );
    }
    @GetMapping("/my")
    @PreAuthorize("hasRole('TALENT')")
    public ResponseEntity<List<MyApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                jobApplicationService.getMyApplications(user)
        );
    }
    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @PathVariable String applicationId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JobApplicationStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                jobApplicationService.updateApplicationStatus(applicationId, user, request)
        );
    }
}
