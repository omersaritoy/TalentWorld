package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.entity.User;

import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthResponse signup(SignupRequest request) {
        logger.info("Kayıt isteği alındı: email={}" + request.email());
        if (userRepository.existsByEmail(request.email())) {
            logger.warning("Kayıt başarısız - email zaten mevcut: {}" + request.email());

            throw new BusinessException("User email already exist",
                    "EMAIL_ALREADY_EXIST",
                    HttpStatus.CONFLICT);
        }
        User user = SignupRequest.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        logger.info(String.format("User created: id=%s, email=%s", user.getId(), request.email()));
        String token = jwtService.generateJwtToken(user);

        return AuthResponse.from(user, "Bearer " + token);
    }

    public AuthResponse signin(SignInRequest request) {
        logger.info(String.format("Giriş isteği alındı: email=%s", request.email()));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            User user = (User) authentication.getPrincipal();

            logger.info(String.format("Giriş başarılı: id={}, email={}", user.getId(), request.email()));

            String token = jwtService.generateJwtToken(user);
            return AuthResponse.from(user, "Bearer " + token);

        } catch (Exception e) {
            logger.warning(String.format("Giriş başarısız: email={}, sebep={}", request.email(), e.getMessage()));
            throw e;
        }
    }
}
