package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.enums.WorkType;
import com.TalentWorld.backend.excepiton.GlobalExceptionHandler;
import com.TalentWorld.backend.service.impl.JobPostServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.Collections;
import java.util.Set;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JobPostIT {
    private MockMvc mockMvc;
    private JobPostServiceImpl jobPostService;
    private ObjectMapper objectMapper;
    private JobPostController jobPostController;
    private JobPostCreateRequest request;
    private Authentication authentication;
    private User user;
    private JobPostResponse response;
    private JobPost post;

    @BeforeEach
    void setup() {
        jobPostService= Mockito.mock(JobPostServiceImpl.class);
        jobPostController=new JobPostController(jobPostService);
        authentication=Mockito.mock(Authentication.class);
        mockMvc = MockMvcBuilders.standaloneSetup(jobPostController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        objectMapper = new ObjectMapper();

        request=new JobPostCreateRequest(
                "title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                2, 4,
                Set.of("Java", "Spring Boot", "Sql"));
        user=new User();
        user.setId("user-123");
        user.setRoles(Collections.singleton(Role.ROLE_RECRUITER));
        post=new JobPost();
        post.setId("post-123");
        post.setUser(user);

        post.setTitle("title");
        post.setDescription("description");
        post.setLocation("location");
        post.setWorkType(WorkType.HYBRID);

        response= JobPostResponse.toDto(post);
    }

    @Test
    void createJobPost_ShouldReturnJobPostResponseWhenJobPostIsValid() throws Exception {
        when(authentication.getPrincipal()).thenReturn(user);
        when(jobPostService.createJobPost(user,request)).thenReturn(response);

        mockMvc.perform(post("/api/jobPost")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("post-123"));


    }

}
