package com.TalentWorld.backend.controller;


import com.TalentWorld.backend.dto.request.SignInRequest;
import com.TalentWorld.backend.dto.request.SignupRequest;
import com.TalentWorld.backend.dto.response.AuthResponse;
import com.TalentWorld.backend.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name="Auth",description = "Authentication operations")
public class AuthController {
    private final AuthService authService;


    @Operation(summary = "Sign up",description = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "Email already taken")
    })
    @PostMapping
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }


    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid login information")
    })
    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> signing(@Valid @RequestBody SignInRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }

    @Operation(summary = "Logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

}
