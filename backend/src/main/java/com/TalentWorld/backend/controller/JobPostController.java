package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.request.JobPostUpdateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Job Posts", description = "Job post management operations")
@RestController
@RequestMapping("/api/jobPost")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;

    @Operation(summary = "Create job post", description = "Creates a new job post. Requires RECRUITER role")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job post created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - RECRUITER role required")
    })
    @PostMapping
    public ResponseEntity<JobPostResponse> create(Authentication authentication, @Valid @RequestBody JobPostCreateRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(jobPostService.createJobPost(user, request));
    }
    @Operation(summary = "Get all job posts", description = "Returns job posts with optional filtering, pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved job posts")
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllJobPosts(
            @RequestParam(required = false) String field,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {


        if (field == null && page == null && size == null) {
            return ResponseEntity.ok(jobPostService.getJobPosts());
        }

        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;

        if (field != null && !field.isEmpty()) {
            return ResponseEntity.ok(jobPostService.findJobsWithPageAndSort(field, p, s));
        }
        return ResponseEntity.ok(jobPostService.findJobsWithPage(p, s));
    }

    @Operation(summary = "Get job post by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job post found"),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    @GetMapping("/byId/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable String id) {
        return ResponseEntity.ok(jobPostService.getJobPostById(id));
    }

    @Operation(summary = "Update job post", description = "Updates an existing job post. Only owner or admin can update")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job post updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPostResponse> update(
            @PathVariable String id,
            Authentication authentication,
            @RequestBody JobPostUpdateRequest request
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(jobPostService.updateJobPost(user, request, id));
    }
    @Operation(summary = "Delete job post", description = "Deletes a job post. Only owner or admin can delete")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Job post not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteJobPostById(@PathVariable String id) {
        return ResponseEntity.ok(jobPostService.deleteJobPostById(id));
    }

}
