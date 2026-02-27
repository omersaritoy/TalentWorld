package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.TalentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/talentProfile")
@RequiredArgsConstructor
public class TalentProfileController {

    private final TalentService talentService;


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> getTalentProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(talentService.getMyProfile(user));
    }
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> create(
            Authentication auth,
            @RequestBody TalentProfileRequest request
    ) {
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(talentService.createProfile(currentUser, request));
    }
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> patch(
            Authentication auth,
            @RequestBody TalentProfilePatchRequest request
    ) {
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(talentService.updateProfile(currentUser, request));
    }
}
