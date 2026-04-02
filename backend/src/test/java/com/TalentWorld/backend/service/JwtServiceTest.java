package com.TalentWorld.backend.service;

import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.service.impl.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class JwtServiceTest {
    private JwtService jwtService;
    private User testUser;

    // Test için geçerli bir Base64 encoded secret (256-bit)
    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckp3dFRlc3RpbmdQdXJwb3NlczEyMzQ1Njc4OTA=";
    private static final long TEST_EXPIRATION = 1000 * 60 * 60; // 1 saat

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // @Value inject edilemiyor, reflection ile set ediyoruz
        Field secretField = JwtService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtService, TEST_SECRET);
        Field expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, TEST_EXPIRATION);

        testUser = new User();
        testUser.setEmail("john@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRoles(Set.of(Role.ROLE_USER));
    }

    @Test
    void generateJwtToken_ShouldReturnToken_WhenUserIsValid() {
        String token = jwtService.generateJwtToken(testUser);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }
    @Test
    void extractEmail_ShouldReturnEmail_WhenTokenIsValid() {
        String token = jwtService.generateJwtToken(testUser);

        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("john@example.com");
    }
    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateJwtToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, "john@example.com");

        assertThat(isValid).isTrue();
    }
    @Test
    void isTokenValid_ShouldReturnFalse_WhenEmailDoesNotMatch() {
        String token = jwtService.generateJwtToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, "different@example.com");

        assertThat(isValid).isFalse();
    }
    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsExpired() throws Exception {
        // Expiration'ı -1 yaparak token'ı anında expire ettiriyoruz
        Field expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, -1000L);

        String expiredToken = jwtService.generateJwtToken(testUser);

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, "john@example.com"))
                .isInstanceOf(ExpiredJwtException.class);
    }
    @Test
    void extractEmail_ShouldThrowException_WhenTokenIsMalformed() {
        String malformedToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtService.extractEmail(malformedToken))
                .isInstanceOf(JwtException.class);
    }

}

