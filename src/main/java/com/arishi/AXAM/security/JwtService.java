package com.arishi.AXAM.security;

import com.arishi.AXAM.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiry-ms:900000}")
    private long accessTokenExpiryMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Users user) {

        return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId()).claim("role", user.getRole() != null ? user.getRole().getName() : null).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs)).signWith(getKey()).compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public void validateToken(String token, UserDetails userDetails) {

        String email = extractUsername(token);

        if (!email.equals(userDetails.getUsername())) {
            throw new JwtException("Invalid JWT token.");
        }

        if (isTokenExpired(token)) {
            throw new JwtException("JWT token has expired.");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();

        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {

        Date expiration = extractClaim(token, Claims::getExpiration);

        return expiration.before(new Date());
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(Users user) {

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenExpiryMs);

        return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId()).claim("role", user.getRole().getName()).issuedAt(Date.from(now)).expiration(Date.from(expiry)).signWith(getSigningKey()).compact();
    }

}