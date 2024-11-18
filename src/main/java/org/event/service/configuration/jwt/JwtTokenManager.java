package org.event.service.configuration.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.event.service.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenManager {
    private final SecretKey key;
    private final long expirationTime;


    public JwtTokenManager(
            @Value("${jwt.secret-key}") String key,
            @Value("${jwt.lifetime}") long expirationTime
    ) {
        this.key = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );
        this.expirationTime = expirationTime;
    }

    public String generateJwt(User user) {
        return Jwts
                .builder()
                .subject(user.login())
                .claim("role", user.role())
                .claim("id", user.id())
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

    public Long getIdFromJwt(String jwt) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("id", Long.class);
    }
}

