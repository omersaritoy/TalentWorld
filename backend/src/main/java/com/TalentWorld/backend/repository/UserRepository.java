package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
