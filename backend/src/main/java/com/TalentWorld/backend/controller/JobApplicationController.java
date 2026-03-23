package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.MyApplicationResponse;
import com.TalentWorld.backend.dto.response.PaginationResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Job Applications", description = "Job application management operations")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @Operation(summary = "Apply to job", description = "Submits a job application. Requires TALENT role")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application submitted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - TALENT role required"),
            @ApiResponse(responseCode = "404", description = "Job post not found"),
            @ApiResponse(responseCode = "409", description = "Already applied to this job")
    })
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

    @Operation(summary = "Get applications for job post", description = "Returns all applications for a job post. Only owner or admin")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applications"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
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

    @Operation(summary = "Get my applications", description = "Returns all applications of the current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved applications")
    @GetMapping("/my")
    @PreAuthorize("hasRole('TALENT')")
    public ResponseEntity<List<MyApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                jobApplicationService.getMyApplications(user)
        );
    }

    @Operation(summary = "Get All Job Applications",description = "Returns all application")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved applications")
    @GetMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<PaginationResponse<JobApplicationResponse>> getJobApplications(
            @RequestParam(required = false) String field,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (field != null && !field.isEmpty()) {
            return ResponseEntity.ok(jobApplicationService.findApplicationsWithPageAndSort(field, page, size));
        }
        return ResponseEntity.ok(jobApplicationService.findJobApplicationsWithPage(page, size));
    }

    @Operation(summary = "Update application status", description = "Updates the status of an application. Only recruiter or admin")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
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
