package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse signup(SignupRequest request) {
        User newUser=SignupRequest.toUser(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser=userRepository.save(newUser);
        System.out.println(savedUser);
        return UserResponse.toDto(savedUser);
    }
}
