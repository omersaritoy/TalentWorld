package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.impl.AuthService;
import com.TalentWorld.backend.service.impl.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Objects;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;
    private AuthenticationManager authenticationManager;
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


    // Signup tests
    // step 1 - write test name
    @Test
    @DisplayName("Should return AuthResponse")
    void signup_ShouldReturnAuthResponse_WhenEmailIsNew() {
        //given
        //step 2 - test data
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

        //step 3 - check dependency services behavior
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateJwtToken(any(User.class))).thenReturn("mock.jwt.token");

        //step 4 - get test method
        AuthResponse authResponse = authService.signup(request);
        //step 5 - check test result and actual result
        assertEquals("Bearer mock.jwt.token", authResponse.token());
        assertNotNull(authResponse);
        assertNotNull(authResponse.roles());


        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(any());

    }

    @Test
    void signup_ShouldThrowBusinessException_WhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest(
                "John",
                "Doe",
                "john@example.com",
                "Password1@",
                Set.of(Role.ROLE_USER));

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        //when & then
        assertThrows(BusinessException.class, () -> authService.signup(request));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }


    @Test
    void signin_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // GIVEN
        SignInRequest request = new SignInRequest("john@example.com", "Password1@");

        User user = SignupRequest.toUser(new SignupRequest(
                "John", "Doe", "john@example.com", "Password1@", Set.of(Role.ROLE_USER)
        ));
        user.setId("uuid-1234");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateJwtToken(user)).thenReturn("mock.jwt.token");

        // WHEN
        AuthResponse response = authService.signin(request);

        // THEN
        assertNotNull(response);
        assertEquals("Bearer mock.jwt.token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateJwtToken(user);
    }

}
