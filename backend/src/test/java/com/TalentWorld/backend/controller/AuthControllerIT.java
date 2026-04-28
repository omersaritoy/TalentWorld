package com.TalentWorld.backend.controller;


import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.excepiton.GlobalExceptionHandler;
import com.TalentWorld.backend.service.impl.AuthService;

import com.TalentWorld.backend.service.impl.JwtService;
import com.TalentWorld.backend.service.impl.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TokenBlacklistService tokenBlacklistService;
    private JwtService jwtService;


    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        tokenBlacklistService = Mockito.mock(TokenBlacklistService.class);
        jwtService = Mockito.mock(JwtService.class);
        authController = new AuthController(authService, tokenBlacklistService,jwtService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())//bussines exception hemen fırlatmamamsı için
                .setValidator(new LocalValidatorFactoryBean()) // @Valid anatasyonu çalışması için
                .build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void signup_ShouldReturnAuthResponse_WhenRequestIsValid() throws Exception {

        SignupRequest signupRequest = new SignupRequest(
                "John", "Doe", "john@example.com", "Password1@",
                Set.of(Role.ROLE_USER)
        );

        AuthResponse authResponse = new AuthResponse(
                "Bearer jwtToken", "john@example.com", Set.of("ROLE_USER")
        );

        when(authService.signup(any(SignupRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.is("Bearer jwtToken")))
                .andExpect(jsonPath("$.username", Matchers.is("john@example.com")))
                .andExpect(jsonPath("$.roles[0]", Matchers.is("ROLE_USER")));
    }

    @Test
    void signup_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {

        SignupRequest signupRequest = new SignupRequest(
                "John", "Doe", "john@example.com", "Password1@",
                Set.of(Role.ROLE_USER)
        );

        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new BusinessException(
                        "Email already exists",
                        "EMAIL_ALREADY_EXIST",
                        HttpStatus.CONFLICT
                ));

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.is("Email already exists")));
    }

    @Test
    void signin_ShouldReturnAuthResponse_WhenRequestIsValid() throws Exception {
        SignInRequest registerRequest = new SignInRequest("john@example.com", "Password@1");
        AuthResponse authResponse = new AuthResponse(
                "mock.token",
                "john@example.com",
                Set.of("ROLE_USER")
        );
        when(authService.signin(any(SignInRequest.class))).thenReturn(authResponse);
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.is("mock.token")))
                .andExpect(jsonPath("$.username", Matchers.is("john@example.com")))
                .andExpect(jsonPath("$.roles[0]", Matchers.is("ROLE_USER")));
    }

    @Test
    void signin_ShouldReturnUnauthorized_WhenPasswordIsWrong() throws Exception {
        SignInRequest signInRequest = new SignInRequest("john@example.com", "Password@1");
        when(authService.signin(any(SignInRequest.class)))
                .thenThrow(new BusinessException(
                        "Invalid email or password",
                        "INVALID_CREDENTIALS",
                        HttpStatus.UNAUTHORIZED
                ));

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", Matchers.is("Invalid email or password")));


    }

    @Test
    void signin_ShouldReturnUnauthorized_WhenEmailNotValid() throws Exception {
        SignInRequest signInRequest = new SignInRequest("notfound@example.com", "Password@1");

        when(authService.signin(any(SignInRequest.class)))
                .thenThrow(new BusinessException(
                        "Invalid email or password",
                        "INVALID_CREDENTIALS",
                        HttpStatus.UNAUTHORIZED
                ));

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }


}

