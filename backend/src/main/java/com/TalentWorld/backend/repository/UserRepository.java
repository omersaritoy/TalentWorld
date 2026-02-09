package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    List<User> findByIsActive(Boolean isActive);

    void delete(Optional<User> user);
}
