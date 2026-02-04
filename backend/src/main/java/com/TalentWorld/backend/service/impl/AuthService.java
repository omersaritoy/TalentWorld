package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String signup(SignupRequest request) {
        User savedUser = SignupRequest.toUser(request);
        savedUser.setPassword(passwordEncoder.encode(savedUser.getPassword()));
        savedUser = userRepository.save(savedUser);
        System.out.println(savedUser);


        if (savedUser == null) {
            return "User not saved";
        }

        return savedUser.getEmail();
    }

    public String singin(SignInRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            return "User signed in successfully";
        } catch (AuthenticationException e) {
            return "Invalid credentials";
        }
    }
}
