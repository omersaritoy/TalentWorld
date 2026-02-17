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
            // diğer alanlar
            userRepository.save(admin);
        }
    }
}

