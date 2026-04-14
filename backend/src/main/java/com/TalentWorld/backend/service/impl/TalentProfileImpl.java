package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.TalentProfilePatchRequest;
import com.TalentWorld.backend.dto.request.TalentProfileRequest;
import com.TalentWorld.backend.dto.response.TalentProfileResponse;
import com.TalentWorld.backend.entity.TalentProfile;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.TalentProfileRepository;
import com.TalentWorld.backend.service.TalentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.Objects;
@Service
@RequiredArgsConstructor
@Slf4j
public class TalentProfileImpl implements TalentService {
    private final TalentProfileRepository repository;

    @Cacheable(value = "talentProfile", key="#currentUser.id")
    @Override
    public TalentProfileResponse getMyProfile(User currentUser) {
        log.info("Profil getirme isteği: userId={}", currentUser.getId());

        TalentProfile profile = repository.findByUserId(currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Profil bulunamadı: userId={}", currentUser.getId());
                    return new BusinessException(
                            "Talent Profile Not Found",
                            "PROFILE_NOT_FOUND",
                            HttpStatus.NOT_FOUND);
                });

        log.info("Profil getirildi: userId={}", currentUser.getId());
        return TalentProfileResponse.toDto(profile);
    }

    @Override
    public TalentProfileResponse createProfile(User currentUser, TalentProfileRequest request) {
        log.info("Profil oluşturma isteği: userId={}", currentUser.getId());

        if (repository.existsByUserId(currentUser.getId())) {
            log.warn("Profil zaten mevcut: userId={}", currentUser.getId());
            throw new BusinessException(
                    "Profile already exist",
                    "PROFILE_ALREADY_EXISTS",
                    HttpStatus.CONFLICT
            );
        }

        TalentProfile profile = new TalentProfile();
        profile.setUser(currentUser);
        request.applyTo(profile);
        TalentProfile saved = repository.save(profile);

        log.info("Profil oluşturuldu: id={}, userId={}", saved.getId(), currentUser.getId());
        return TalentProfileResponse.toDto(saved);
    }

    @CacheEvict(value = "talentProfile", key="#currentUser.id")
    @Override
    public TalentProfileResponse updateProfile(User currentUser, TalentProfilePatchRequest request) {
        log.info("Profil güncelleme isteği: userId={}", currentUser.getId());

        TalentProfile profile = repository.findByUserId(currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Güncellenecek profil bulunamadı: userId={}", currentUser.getId());
                    return new BusinessException(
                            "Profile not found",
                            "PROFILE_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        request.applyTo(profile);
        log.info("Profil güncellendi: userId={}", currentUser.getId());
        return TalentProfileResponse.toDto(repository.save(profile));
    }

    @Override
    public String deleteProfile(User currentUser, String talentProfileId) {
        log.info("Profil silme isteği: talentProfileId={}, userId={}", talentProfileId, currentUser.getId());

        TalentProfile talentProfile = repository.findById(talentProfileId)
                .orElseThrow(() -> {
                    log.warn("Silinecek profil bulunamadı: talentProfileId={}", talentProfileId);
                    return new BusinessException(
                            "Talent Profile not found",
                            "PROFILE_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        if (!isAdmin && !talentProfile.getUser().getId().equals(currentUser.getId())) {
            log.warn("Yetkisiz profil silme girişimi: talentProfileId={}, userId={}",
                    talentProfileId, currentUser.getId());
            throw new BusinessException(
                    "You are not authorized to delete this profile",
                    "UNAUTHORIZED",
                    HttpStatus.FORBIDDEN
            );
        }

        repository.delete(talentProfile);
        log.info("Profil silindi: talentProfileId={}, userId={}", talentProfileId, currentUser.getId());
        return "Talent Profile Deleted Successfully by id: " + talentProfileId;
    }
}