package com.TalentWorld.backend;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BackendApplication(UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
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

