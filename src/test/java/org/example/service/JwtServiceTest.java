package org.example.service;

import io.jsonwebtoken.Jwts;
import org.example.dto.jwt.AuthRequestDto;
import org.example.dto.jwt.AuthResponseDto;
import org.example.exception.jwt.ExpiredToken;
import org.example.exception.jwt.InvalidCreds;
import org.example.exception.jwt.WrongToken;
import org.example.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtServiceImpl jwtService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetails userDetails;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final int HALF_HOUR = 1000 * 60 * 30;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(messageSource, authenticationManager);
    }

    @Test
    void generateToken_validCredentials_returnsAuthToken() {
        AuthRequestDto authRequest = new AuthRequestDto(USERNAME, PASSWORD);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword(),
                        new HashSet<Role>()
                ));

        AuthResponseDto authResponse = jwtService.generateToken(authRequest);

        assertNotNull(authResponse);
        assertFalse(authResponse.getToken().isEmpty());
    }

    @Test
    void generateToken_invalidCredentials_throwsInvalidCredsException() {
        AuthRequestDto authRequest = new AuthRequestDto(USERNAME, PASSWORD);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {
                });

        assertThrows(InvalidCreds.class, () -> jwtService.generateToken(authRequest));
    }

    @Test
    void validateToken_validTokenAndUserDetails_returnsTrue() {

        String token = generateValidToken();
        when(userDetails.getUsername()).thenReturn(USERNAME);

        boolean result = jwtService.validateToken(token, userDetails);

        assertTrue(result);
    }

    @Test
    void validateToken_invalidToken_throwsWrongTokenException() {
        String invalidToken = "invalidToken";

        assertThrows(WrongToken.class, () -> jwtService.validateToken(invalidToken, userDetails));
    }

    @Test
    void validateToken_expiredToken_throwsExpiredTokenException() {
        String expiredToken = generateExpiredToken();

        assertThrows(ExpiredToken.class, () -> jwtService.validateToken(expiredToken, userDetails));
    }

    private String generateValidToken() {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + HALF_HOUR))
                .subject(USERNAME)
                .signWith(jwtService.key)
                .compact();
    }

    private String generateExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() - HALF_HOUR))
                .subject("userName")
                .signWith(jwtService.key)
                .compact();
    }
}
