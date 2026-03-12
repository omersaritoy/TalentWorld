package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.PaginationResponse;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.TalentProfile;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.TalentProfileRepository;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.TalentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TalentProfileImpl implements TalentService {
    private final TalentProfileRepository repository;
    private final UserRepository userRepository;

    @Override
    public TalentProfileResponse getMyProfile(User currentUser) {
        TalentProfile profile = repository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException(
                        "Talent Profile Not Found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));
        return TalentProfileResponse.toDto(profile);
    }

    @Override
    public TalentProfileResponse createProfile(User currentUser, TalentProfileRequest request) {
        if (repository.existsByUserId(currentUser.getId())) {
            throw new BusinessException(
                    "Profile already exist",
                    "PROFILE_ALREADY_EXISTS",
                    HttpStatus.CONFLICT
            );
        }

        TalentProfile profile = new TalentProfile();
        profile.setUser(currentUser);
        request.applyTo(profile);
        return TalentProfileResponse.toDto(repository.save(profile));
    }

    @Override
    public TalentProfileResponse updateProfile(User currentUser, TalentProfilePatchRequest request) {
        TalentProfile profile = repository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException(
                        "Profile not found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        request.applyTo(profile);
        return TalentProfileResponse.toDto(profile);
    }

    @Override
    public String deleteProfile(User currentUser,String talentProfileId) {

        TalentProfile talentProfile = repository.findById(talentProfileId)
                .orElseThrow(() -> new BusinessException(
                        "Talent Profile not found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        if (!isAdmin && !talentProfile.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "You are not authorized to delete this profile",
                    "UNAUTHORIZED",
                    HttpStatus.FORBIDDEN
            );
        }

        repository.delete(talentProfile);
        return "Talent Profile Deleted Successfully by id: " + talentProfileId;
    }


}
