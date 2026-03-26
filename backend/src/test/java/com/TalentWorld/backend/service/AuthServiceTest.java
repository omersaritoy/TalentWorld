package com.TalentWorld.backend.service;

import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.impl.AuthService;
import com.TalentWorld.backend.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private AuthService authService;
    private JwtService jwtService;


    @BeforeEach
    public void setup() {
        userRepository= Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        authService=new AuthService(userRepository,passwordEncoder,authenticationManager,jwtService);

    }
}
