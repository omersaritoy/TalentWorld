package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.request.JobPostUpdateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobPost")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPostResponse> create(Authentication authentication, @Valid @RequestBody JobPostCreateRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(jobPostService.createJobPost(user, request));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        return ResponseEntity.ok(jobPostService.getJobPosts());
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable String id) {
        return ResponseEntity.ok(jobPostService.getJobPostById(id));
    }

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteJobPostById(@PathVariable String id) {
        return ResponseEntity.ok(jobPostService.deleteJobPostById(id));
    }

}
