package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.jwt.AuthRequestDto;
import org.example.dto.jwt.AuthResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

@Validated
public interface JwtService {
    AuthResponseDto generateToken(@Valid AuthRequestDto authRequest);

    Boolean validateToken(String token, UserDetails userDetails);

    String extractUsername(String token);
}
