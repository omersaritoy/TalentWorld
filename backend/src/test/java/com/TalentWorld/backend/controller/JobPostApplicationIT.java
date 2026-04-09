package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.ApplicationStatus;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.excepiton.GlobalExceptionHandler;
import com.TalentWorld.backend.service.impl.JobApplicationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JobPostApplicationIT {

    private JobApplicationServiceImpl jobApplicationService;
    private JobApplicationController jobApplicationController;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private JobPost jobPost;
    private User talentUser;
    private User recruiterUser;


    @BeforeEach
    void setUp() {
        jobApplicationService = Mockito.mock(JobApplicationServiceImpl.class);
        jobApplicationController = new JobApplicationController(jobApplicationService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(jobApplicationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        mapper = new ObjectMapper();


        talentUser = new User();
        talentUser.setId("user-123");
        talentUser.setEmail("talent@test.com");
        talentUser.setRoles(Collections.singleton(Role.ROLE_USER));

        recruiterUser = new User();
        recruiterUser.setId("recruiter-123");
        recruiterUser.setEmail("recruiter@test.com");
        recruiterUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        jobPost = new JobPost();
        jobPost.setId("post-123");
        jobPost.setTitle("Backend Developer");
        jobPost.setDescription("Spring Boot Developer needed");
        jobPost.setIsActive(true);
        jobPost.setUser(recruiterUser);

    }

    private void authenticateAs(User user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void applyToJob_ShouldReturn200_WhenRequestIsValid() throws Exception {
        authenticateAs(talentUser);

        JobApplicationCreateRequest request = new JobApplicationCreateRequest("I am a great fit.");

        JobApplicationResponse response = new JobApplicationResponse(
                "app-1", "post-123", "Backend Developer", "user-123", "", ApplicationStatus.APPLIED,
                "I am a great fit.", LocalDateTime.now()
        );

        when(jobApplicationService.applyToJob(eq("post-123"), any(User.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/applications/job-posts/post-123/apply")
                        .principal(new UsernamePasswordAuthenticationToken(talentUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("app-1"))
                .andExpect(jsonPath("$.jobPostId").value("post-123"))
                .andExpect(jsonPath("$.talentId").value("user-123"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void applyToJob_ShouldReturn400_WhenCoverLetterIsBlank() throws Exception {
        authenticateAs(talentUser);

        String body = "{\"coverLetter\": \"\"}";

        mockMvc.perform(post("/api/applications/job-posts/post-123/apply")
                        .principal(new UsernamePasswordAuthenticationToken(talentUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void applyToJob_ShouldReturn4xx_WhenAlreadyApplied() throws Exception {
        authenticateAs(talentUser);

        JobApplicationCreateRequest request = new JobApplicationCreateRequest("Applying again.");

        when(jobApplicationService.applyToJob(eq("post-123"), any(User.class), any()))
                .thenThrow(new BusinessException("Already applied to this job", "ALREADY_APPLIED", HttpStatus.CONFLICT));

        mockMvc.perform(post("/api/applications/job-posts/post-123/apply")
                        .principal(new UsernamePasswordAuthenticationToken(talentUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict());


    }
    @Test
    void applyToJob_ShouldReturn4xx_WhenJobPostNotFound() throws Exception {
        authenticateAs(talentUser);

        JobApplicationCreateRequest request = new JobApplicationCreateRequest("Cover letter.");

        when(jobApplicationService.applyToJob(eq("nonexistent"), any(User.class), any()))
                .thenThrow(new BusinessException("Job post not found","POST_NOT_FOUND", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/applications/job-posts/nonexistent/apply")
                        .principal(new UsernamePasswordAuthenticationToken(talentUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    @Test
    void getApplicationsForJob_ShouldReturn200WithList_WhenUserIsOwner() throws Exception {
        authenticateAs(recruiterUser);

        JobApplicationResponse response = new JobApplicationResponse(
                "app-1", "post-123", "Backend Developer", "user-123", "", ApplicationStatus.APPLIED,
                "I am a great fit.", LocalDateTime.now()
        );

        when(jobApplicationService.getApplicationsForJob(eq("post-123"), any(User.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/applications/job-posts/post-123")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("app-1"));
    }
    @Test
    void getApplicationsForJob_ShouldReturn200WithEmptyList_WhenNoApplications() throws Exception {
        authenticateAs(recruiterUser);

        when(jobApplicationService.getApplicationsForJob(eq("post-123"), any(User.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/applications/job-posts/post-123")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getApplicationsForJob_ShouldReturn4xx_WhenUserIsNotOwner() throws Exception {
        authenticateAs(recruiterUser);

        when(jobApplicationService.getApplicationsForJob(eq("post-123"), any(User.class)))
                .thenThrow(new BusinessException("Access denied","UNAUTHORIZED", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(get("/api/applications/job-posts/post-123")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getApplicationsForJob_ShouldReturn4xx_WhenJobPostNotFound() throws Exception {
        authenticateAs(recruiterUser);

        when(jobApplicationService.getApplicationsForJob(eq("nonexistent"), any(User.class)))
                .thenThrow(new BusinessException("Job post not found","POST_NOT_FOUND", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/applications/job-posts/nonexistent")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateApplicationStatus_ShouldReturn200_WhenUserIsOwner() throws Exception {
        authenticateAs(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.REVIEWING);

        JobApplicationResponse response = new JobApplicationResponse(
                "app-1", "post-123", "Backend Developer","user-123","", ApplicationStatus.REVIEWING,
                "Cover letter", LocalDateTime.now()
        );

        when(jobApplicationService.updateApplicationStatus(eq("app-1"), any(User.class), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/applications/app-1/status")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("app-1"))
                .andExpect(jsonPath("$.status").value("REVIEWED"));
    }

    @Test
    void updateApplicationStatus_ShouldReturn200_WhenStatusIsRejected() throws Exception {
        authenticateAs(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.REJECTED);

        JobApplicationResponse response = new JobApplicationResponse(
                "app-1", "post-123", "Backend Developer","user-123","", ApplicationStatus.REJECTED,
                "Cover letter", LocalDateTime.now()
        );

        when(jobApplicationService.updateApplicationStatus(eq("app-1"), any(User.class), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/applications/app-1/status")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void updateApplicationStatus_ShouldReturn4xx_WhenApplicationNotFound() throws Exception {
        authenticateAs(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.APPLIED);

        when(jobApplicationService.updateApplicationStatus(eq("nonexistent"), any(User.class), any()))
                .thenThrow(new BusinessException("Application not found","APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND));

        mockMvc.perform(patch("/api/applications/nonexistent/status")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateApplicationStatus_ShouldReturn4xx_WhenUserIsNotOwnerOrAdmin() throws Exception {
        User otherRecruiter = new User();
        otherRecruiter.setId("other-recruiter");
        otherRecruiter.setRoles(Collections.singleton(Role.ROLE_RECRUITER));
        authenticateAs(otherRecruiter);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.APPLIED);

        when(jobApplicationService.updateApplicationStatus(eq("app-1"), any(User.class), any()))
                .thenThrow(new BusinessException("Access denied","UNAUTHORIZED", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(patch("/api/applications/app-1/status")
                        .principal(new UsernamePasswordAuthenticationToken(otherRecruiter, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateApplicationStatus_ShouldReturn400_WhenRequestBodyIsMissing() throws Exception {
        authenticateAs(recruiterUser);

        mockMvc.perform(patch("/api/applications/app-1/status")
                        .principal(new UsernamePasswordAuthenticationToken(recruiterUser, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


}