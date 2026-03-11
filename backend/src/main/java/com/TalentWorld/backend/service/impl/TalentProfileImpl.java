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
        User talent = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new BusinessException(
                        "Profile not Found",
                        "PROFILE_NOT_FOUND",
                        HttpStatus.NOT_FOUND

                ));
        repository.delete(talentProfileId);

        return "Talent Profile Deleted Successfully by id: " + talentProfileId;
    }


}
