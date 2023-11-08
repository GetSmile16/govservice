package org.example.service;

import io.jsonwebtoken.Claims;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// TODO: JwtServiceImpl

@Component
public class JwtService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final MessageSource messageSource;
    private final AuthenticationManager authenticationManager;

    public final SecretKey key;

    @Autowired
    public JwtService(MessageSource messageSource, @Value("${jwt.secret}") String secret, AuthenticationManager authenticationManager) {
        this.messageSource = messageSource;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto generateToken(AuthRequestDto authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    ));
        } catch (AuthenticationException e) {
            String message = messageSource.getMessage("user.invalid_creds", null, Locale.getDefault());
            log.warn(message);
            throw new InvalidCreds(message);
        }
        return new AuthResponseDto(createToken(authRequest.getUsername()));
    }

    public String createToken(String userName) {
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

    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private Claims extractAllClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            String message = messageSource.getMessage("token.is_wrong", null, Locale.getDefault());
            log.warn(message);
            throw new WrongToken(message);
        }
        return claims;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean equals = username.equals(userDetails.getUsername());
        if (!equals) {
            String message = messageSource.getMessage("token.is_wrong", null, Locale.getDefault());
            log.warn(message);
            throw new WrongToken(message);
        }

        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            String message = messageSource.getMessage("token.is_expired", null, Locale.getDefault());
            log.warn(message);
            throw new ExpiredToken(message);
        }
        return true;
    }

}

