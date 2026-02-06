package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse signup(SignupRequest request) {
        User user = SignupRequest.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_ADMIN));
        user = userRepository.save(user);

        String token = jwtService.generateJwtToken(user);

        return AuthResponse.from(user, "Bearer " + token);
    }

    public AuthResponse singin(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateJwtToken(user);
        return AuthResponse.from(user, "Bearer " + token);
    }
}
