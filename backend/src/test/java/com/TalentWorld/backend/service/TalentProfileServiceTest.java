package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.TalentProfile;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.TalentProfileRepository;
import com.TalentWorld.backend.service.impl.TalentProfileImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class TalentProfileServiceTest {
    @Mock
    private TalentProfileRepository repository;

    @InjectMocks
    private TalentProfileImpl talentProfileService;

    private User currentUser;
    private TalentProfile talentProfile;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        currentUser = new User();
        currentUser.setId("user-123");
        currentUser.setEmail("john@example.com");
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");

        talentProfile = new TalentProfile();
        talentProfile.setId("profile-123");
        talentProfile.setTitle("Backend Developer");
        talentProfile.setExperienceYear(3);
        talentProfile.setAbout("Java developer");
        talentProfile.setSkills(Set.of("Java", "Spring Boot", "Docker"));
        talentProfile.setUser(currentUser);


    }
    @Test
    void createProfile_ShouldReturnTalentProfileResponseWhenCredentialIsValid() {
        when(repository.existsByUserId(currentUser.getId())).thenReturn(false);
        when(repository.save(any(TalentProfile.class))).thenReturn(talentProfile);
        TalentProfileRequest request=new TalentProfileRequest(
                "Backend Developer",3,"Java developer",Set.of("Java","Spring Boot","Docker"));


        TalentProfileResponse response = talentProfileService.createProfile(currentUser, request);
        assertNotNull(response);
        assertThat(response.id()).isEqualTo(talentProfile.getId());
        assertThat(response.title()).isEqualTo(talentProfile.getTitle());
        assertThat(response.experienceYear()).isEqualTo(talentProfile.getExperienceYear());
        assertThat(response.about()).isEqualTo(talentProfile.getAbout());
        assertThat(response.skills()).isEqualTo(talentProfile.getSkills());
        verify(repository,times(1)).existsByUserId(currentUser.getId());
        verify(repository, times(1)).save(any(TalentProfile.class));

    }

    @Test
    void createProfile_ShouldThrowBusinessException_WhenProfileAlreadyExists() {
        when(repository.existsByUserId(currentUser.getId())).thenReturn(true);
        TalentProfileRequest request=new TalentProfileRequest(
                "Backend Developer",3,"Java developer",Set.of("Java","Spring Boot","Docker"));

        assertThrows(BusinessException.class,
                () -> talentProfileService.createProfile(currentUser, request));
        verify(repository, never()).save(any());

    }
    @Test
    void updateProfile_ShouldReturnTalentProfileResponseWhenCredentialIsValid() {
        when(repository.existsByUserId(currentUser.getId())).thenReturn(true);
        when(repository.save(any(TalentProfile.class))).thenReturn(talentProfile);
        TalentProfilePatchRequest request=new TalentProfilePatchRequest(null,2,null,null);
        TalentProfileResponse response = talentProfileService.updateProfile(currentUser, request);

        assertNotNull(response);
        assertThat(response.id()).isEqualTo(talentProfile.getId());
        assertThat(response.title()).isEqualTo(talentProfile.getTitle());
        assertThat(response.experienceYear()).isEqualTo(talentProfile.getExperienceYear());
        assertThat(response.about()).isEqualTo(talentProfile.getAbout());
        assertThat(response.skills()).isEqualTo(talentProfile.getSkills());
        verify(repository,times(1)).existsByUserId(currentUser.getId());
        verify(repository,times(1)).save(any(TalentProfile.class));
    }
    @Test
    void updateProfile_ShouldThrowBusinessException_WhenProfileNotFoundExists() {
       when(repository.existsByUserId(currentUser.getId())).thenReturn(true);
       when(repository.findById(currentUser.getId())).thenReturn(Optional.empty());
       TalentProfilePatchRequest request=new TalentProfilePatchRequest(null,2,null,null);


        assertThrows(BusinessException.class,
                () -> talentProfileService.updateProfile(currentUser, request));

        verify(repository, never()).save(any());
    }

    @Test
    void getMyTalentProfile_ShouldReturnTalentProfileResponse() {

        when(repository.findByUserId("user-123")).thenReturn(Optional.of(talentProfile));
        TalentProfileResponse result = talentProfileService.getMyProfile(currentUser);
        assertThat(result.id()).isEqualTo("profile-123");
        assertThat(result.title()).isEqualTo("Backend Developer");
        assertThat(result.experienceYear()).isEqualTo(3);
        assertThat(result.about()).isEqualTo("Java developer");
        assertThat(result.skills().size()).isEqualTo(3);
    }
    @Test
    void getMyProfile_ShouldThrowBusinessException_WhenProfileNotFound() {
        when(repository.findByUserId("user-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> talentProfileService.getMyProfile(currentUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Talent Profile Not Found");

        verify(repository, times(1)).findByUserId("user-123");
    }
    @Test
    void getMyProfile_ShouldThrowBusinessException_WithNotFoundStatus() {
        when(repository.findByUserId("user-123")).thenReturn(Optional.empty());

        BusinessException exception = catchThrowableOfType(
                () -> talentProfileService.getMyProfile(currentUser),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo("PROFILE_NOT_FOUND");
    }


}

