package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.TalentProfile;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TalentProfileIT {
    private MockMvc mockMvc;
    private TalentProfileImpl talentProfileService;
    private TalentProfileController talentProfileController;
    private ObjectMapper mapper;
    private Authentication auth;

    @BeforeEach
    public void setup() {
        talentProfileService = mock(TalentProfileImpl.class);
        talentProfileController = new TalentProfileController(talentProfileService);
        mockMvc = MockMvcBuilders.standaloneSetup(talentProfileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        mapper = new ObjectMapper();
        auth = mock(Authentication.class);

    }

    @Test
    void createProfile_ShouldReturnTalentProfileResponse() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

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
        when(auth.getPrincipal()).thenReturn(currentUser);
        TalentProfileRequest request = new TalentProfileRequest(
                "Backend Developer", 3, "Java developer",
                Set.of("Java"));
        when(talentProfileService.createProfile(currentUser, request)).thenThrow(new BusinessException("User Profile Alredy Exist",
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

    @Test
    void getMyProfile_ShouldReturnNotFound_WhenUserProfileDoesNotExist() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");
        when(auth.getPrincipal()).thenReturn(currentUser);
        when(talentProfileService.getMyProfile(any(User.class))).thenThrow(new BusinessException(
                "Talent Profile Not Found",
                "NOT_FOUND",
                HttpStatus.NOT_FOUND
        ));
        mockMvc.perform(get("/api/talent")
                        .principal(auth))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Talent Profile Not Found"));
    }

    @Test
    void updateProfile_ShouldReturnTalentProfileResponse() throws Exception {

        User currentUser = new User();
        currentUser.setId("user-123");

        when(auth.getPrincipal()).thenReturn(currentUser);

        TalentProfilePatchRequest request = new TalentProfilePatchRequest(
                "Backend Developer",
                3,
                "Java developer",
                Set.of("Java", "Spring Boot", "Docker")
        );

        TalentProfileResponse response = new TalentProfileResponse(
                "user-123",
                "Backend Developer",
                3,
                "Java developer",
                Set.of("Java", "Spring Boot", "Docker")
        );

        when(talentProfileService.updateProfile(any(User.class), any(TalentProfilePatchRequest.class)))
                .thenReturn(response);

        // when + then
        mockMvc.perform(patch("/api/talent")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.experienceYear").value(3));
    }

    @Test
    void updateProfile_ShouldReturn400_WhenRequestInvalid() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

        when(auth.getPrincipal()).thenReturn(currentUser);

        TalentProfilePatchRequest request = new TalentProfilePatchRequest(
                "", 0, "", Set.of()
        );
        when(talentProfileService.updateProfile(any(User.class), any(TalentProfilePatchRequest.class))).thenThrow(
                new BusinessException("Request is invalid",
                        "INVALID REQUEST",
                        HttpStatus.BAD_REQUEST)
        );

        mockMvc.perform(patch("/api/talent")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_ShouldReturn404_WhenProfileNotFound() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

        when(auth.getPrincipal()).thenReturn(currentUser);

        TalentProfilePatchRequest request = new TalentProfilePatchRequest(
                "Backend Developer", 3, "desc", Set.of("Java")
        );

        when(talentProfileService.updateProfile(any(), any()))
                .thenThrow(new BusinessException(
                        "Profile not found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        mockMvc.perform(patch("/api/talent")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProfile_ShouldReturnOkWhenProfileDeleted() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");
        when(auth.getPrincipal()).thenReturn(currentUser);
        TalentProfile profile = new TalentProfile();
        profile.setId("profile-123");
        when(talentProfileService.deleteProfile(currentUser, profile.getId())).thenReturn(
                "Talent Profile Deleted Successfully by id: profile-123");

        mockMvc.perform(delete("/api/talent/profile-123").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Talent Profile Deleted Successfully by id: profile-123"));
        verify(talentProfileService, times(1)).deleteProfile(currentUser, profile.getId());
    }
    @Test
    void deleteProfile_ShouldReturn200_WhenAdminDeletesAnyProfile() throws Exception {
        User adminUser = new User();
        adminUser.setId("admin-1");

        when(auth.getPrincipal()).thenReturn(adminUser);

        when(talentProfileService.deleteProfile(any(), any()))
                .thenReturn("Deleted");

        mockMvc.perform(delete("/api/talent/profile-123")
                        .principal(auth))
                .andExpect(status().isOk());
    }
    @Test
    void deleteProfile_ShouldReturn404_WhenProfileNotFound() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

        when(auth.getPrincipal()).thenReturn(currentUser);

        when(talentProfileService.deleteProfile(any(), any()))
                .thenThrow(new BusinessException(
                        "Profile not found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        mockMvc.perform(delete("/api/talent/profile-123")
                        .principal(auth))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteProfile_ShouldReturn403_WhenUserDeletesAnotherUsersProfile() throws Exception {
        User currentUser = new User();
        currentUser.setId("user-123");

        when(auth.getPrincipal()).thenReturn(currentUser);

        when(talentProfileService.deleteProfile(any(), any()))
                .thenThrow(new BusinessException(
                        "You are not allowed to delete this profile",
                        "FORBIDDEN_ACTION",
                        HttpStatus.FORBIDDEN
                ));

        mockMvc.perform(delete("/api/talent/profile-999")
                        .principal(auth))
                .andExpect(status().isForbidden());
    }
}



