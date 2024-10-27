package org.event.service.configuration.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {
    private final SecretKey key;
    private final long expirationTime;


    public JwtTokenManager(
            @Value("${jwt.secret-key}") String key,
            @Value("${jwt.lifetime}") long expirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
        this.expirationTime = expirationTime;
    }

    public String generateJwt(String login, String role) {
        return Jwts
                .builder()
                .subject(login)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String getLoginFromJwt(String jwt) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public String getRoleFromJwt(String jwt) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("role", String.class);
    }
}

