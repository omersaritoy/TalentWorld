package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.impl.AuthService;
import com.TalentWorld.backend.service.impl.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Objects;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private AuthService authService;
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @AfterEach
    public void teardown() {

    }

    //Signup tests
    @Test
    void signup_ShouldReturnAuthResponse_WhenEmailIsNew() {
        //given
        SignupRequest request = new SignupRequest(
                "John",
                "Doe",
                "john@example.com",
                "Password1@",
                Set.of(Role.ROLE_USER)   // Role enum'una göre düzenle
        );
        User savedUser = new User();
        savedUser.setId("uuid-1234");
        savedUser.setEmail("john@example.com");
        savedUser.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateJwtToken(any(User.class))).thenReturn("mock.jwt.token");

        AuthResponse authResponse = authService.signup(request);

        assertNotNull(authResponse);
        assertEquals("Bearer mock.jwt.token", authResponse.token());
        assertNotNull(authResponse.roles());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(any());

    }

}
