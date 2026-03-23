package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.service.TalentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Talent Profile", description = "Talent profile management operations")
@RestController
@RequestMapping("/api/talent")
@RequiredArgsConstructor
public class TalentProfileController {

    private final TalentService talentService;


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> getTalentProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(talentService.getMyProfile(user));
    }
    @Operation(summary = "Create profile", description = "Creates a talent profile for the current user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile created successfully"),
            @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> create(
            Authentication auth,
            @RequestBody TalentProfileRequest request
    ) {
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(talentService.createProfile(currentUser, request));
    }
    @Operation(summary = "Update profile", description = "Updates the current user's talent profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TalentProfileResponse> patch(
            Authentication auth,
            @RequestBody TalentProfilePatchRequest request
    ) {
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(talentService.updateProfile(currentUser, request));
    }
    @Operation(summary = "Delete profile", description = "Deletes a talent profile. Only owner or admin")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping("/{talentProfileId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteTalentProfile(Authentication authentication, @PathVariable String talentProfileId) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(talentService.deleteProfile(currentUser, talentProfileId));
    }
}
