package com.example.studentmangerment.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

/**
 * Utility component for JWT token creation and validation.
 */
@Component
public class JwtUtils {
    /** HMAC secret used to sign and verify tokens. */
    @Value("${jwtSecret}")
    private String jwtSecret;
    /** Token time-to-live in milliseconds. */
    @Value("${jwtExpiration}")
    private long jwtExpirationMs;

    /**
     * Generates a signed JWT token for a username.
     *
     * @param username authenticated principal name
     * @return signed JWT token string
     */
    public String generateToken(String username) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates token signature and expiration.
     *
     * @param token bearer token value
     * @return {@code true} when token is valid; otherwise {@code false}
     */
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts username (subject) from a valid token.
     *
     * @param token JWT token
     * @return username stored in token subject claim
     */
    public String getUsernameFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
