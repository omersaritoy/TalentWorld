package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.excepiton.GlobalExceptionHandler;
import com.TalentWorld.backend.service.impl.TalentProfileImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TalentProfileIT {
    private MockMvc mockMvc;
    private TalentProfileImpl talentProfileService;
    private TalentProfileController talentProfileController;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        talentProfileService = mock(TalentProfileImpl.class);
        talentProfileController = new TalentProfileController(talentProfileService);
        mockMvc = MockMvcBuilders.standaloneSetup(talentProfileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        mapper = new ObjectMapper();

    }

    @Test
    void createProfile_ShouldReturnTalentProfileResponse() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(currentUser);

        TalentProfileRequest request = new TalentProfileRequest(
                "Backend Developer", 3, "Java developer",
                Set.of("Java", "Spring Boot", "Docker"));

        TalentProfileResponse response = new TalentProfileResponse(
                "profile-123", "Backend Developer", 3,
                "Java developer",
                Set.of("Java", "Spring Boot", "Docker")
        );

        when(talentProfileService.createProfile(any(User.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/talent")
                        .principal(auth) // 🔥 kritik nokta
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("profile-123"))
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.about").value("Java developer"))
                .andExpect(jsonPath("$.experienceYear").value(3));
    }

    @Test
    void createProfile_ShouldReturnConflict_WhenUserProfileAlreadyExist() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(currentUser);
        TalentProfileRequest request = new TalentProfileRequest(
                "Backend Developer", 3, "Java developer",
                Set.of("Java"));
        when(talentProfileService.createProfile(currentUser,request)).thenThrow(new BusinessException("User Profile Alredy Exist",
                "ALREADY_EXIST",
                HttpStatus.CONFLICT
                ));


        mockMvc.perform(post("/api/talent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    void getMyProfile_ShouldReturnTalentProfileResponse() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(currentUser);
        TalentProfileResponse response = new TalentProfileResponse(
                "profile-123", "Backend Developer", 3,
                "Java developer",
                Set.of("Java", "Spring Boot", "Docker")
        );
        when(talentProfileService.getMyProfile(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/talent")
                        .principal(auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("profile-123"))
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.about").value("Java developer"))
                .andExpect(jsonPath("$.experienceYear").value(3));
    }
}



