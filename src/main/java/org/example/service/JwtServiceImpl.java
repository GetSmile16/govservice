package org.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.dto.AuthRequestDto;
import org.example.dto.AuthResponseDto;
import org.example.exception.ExpiredToken;
import org.example.exception.InvalidCreds;
import org.example.exception.WrongToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final MessageSource messageSource;
    private final AuthenticationManager authenticationManager;

    public static final String SECRET = "91ac210feccd4abc4c34514893c28431d0834833d075784dc34d4c5aeb472049";
    public SecretKey key;

    @Autowired
    public JwtServiceImpl(MessageSource messageSource,
                          AuthenticationManager authenticationManager) {
        this.messageSource = messageSource;
        this.authenticationManager = authenticationManager;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    @Override
    public AuthResponseDto generateToken(AuthRequestDto authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            if (!authenticate.isAuthenticated()) {
                throw new UsernameNotFoundException("Invalid username");
            }
        } catch (AuthenticationException e) {
            String message = messageSource.getMessage("user.invalid_creds", null, Locale.getDefault());
            log.warn(message);
            throw new InvalidCreds(message);
        }
        return new AuthResponseDto(createToken(authRequest.getUsername()));
    }

    private String createToken(String userName) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(30)
                .atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .claims(claims)
                .id(id)
                .issuedAt(now)
                .notBefore(now)
                .expiration(exp)
                .subject(userName)
                .signWith(key)
                .compact();

        log.info("Token generated for username \"" + userName + "\"");
        return token;
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            String message = messageSource.getMessage("token.is_expired", null, Locale.getDefault());
            log.warn(message);
            throw new ExpiredToken(message);
        } catch (JwtException e) {
            String message = messageSource.getMessage("token.is_wrong", null, Locale.getDefault());
            log.warn(message);
            throw new WrongToken(message);
        }
        return claims;
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean equals = username.equals(userDetails.getUsername());
        if (!equals) {
            String message = messageSource.getMessage("token.is_wrong", null, Locale.getDefault());
            log.warn(message);
            throw new WrongToken(message);
        }
        return true;
    }

}

