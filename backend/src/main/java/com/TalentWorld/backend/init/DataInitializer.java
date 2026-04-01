package com.TalentWorld.backend.init;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin2@gmail.com")) {
            User admin = new User();
            admin.setEmail("admin2@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123!!"));
            userRepository.save(admin);
        }
        if (!userRepository.existsByEmail("recruiter@gmail.com")) {
            User recruiter = new User();
            recruiter.setEmail("recruiter@gmail.com");
            recruiter.setPassword(passwordEncoder.encode("recruiter123!!"));
            recruiter.setRoles(Collections.singleton(Role.ROLE_RECRUITER));
            userRepository.save(recruiter);
        }
    }
}