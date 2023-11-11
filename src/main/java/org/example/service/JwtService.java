package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.AuthRequestDto;
import org.example.dto.AuthResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

@Validated
public interface JwtService {
    AuthResponseDto generateToken(@Valid AuthRequestDto authRequest);

    Boolean validateToken(String token, UserDetails userDetails);

    String extractUsername(String token);
}
