package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.User;

public interface TalentService {

    TalentProfileResponse getMyProfile(User currentUser);
    TalentProfileResponse createProfile(User currentUser, TalentProfileRequest request);
    TalentProfileResponse updateProfile(User currentUser, TalentProfilePatchRequest request);
}

