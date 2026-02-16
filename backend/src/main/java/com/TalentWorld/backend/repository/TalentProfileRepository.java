package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TalentProfileRepository extends JpaRepository<TalentProfile, String> {

    Optional<TalentProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);
}