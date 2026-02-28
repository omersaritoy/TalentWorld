package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    List<User> findByIsActive(Boolean isActive);


    boolean existsByEmail(String email);
}
