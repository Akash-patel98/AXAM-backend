package com.arishi.AXAM.security;

import com.arishi.AXAM.model.RefreshToken;
import com.arishi.AXAM.model.Users;
import com.arishi.AXAM.repo.RefreshTokenRepository;
import com.arishi.AXAM.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenIssuer {

    private final RefreshTokenRepository refreshTokenRepository;
    private final HashUtil hashUtil;

    @Value("${app.jwt.refresh-token-expiry-days:30}")
    private long refreshTokenExpiryDays;

    public String issue(Users user, HttpServletRequest request) {
        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashUtil.sha256(rawToken));
        refreshToken.setExpiresAt(Instant.now().plusNanos(refreshTokenExpiryDays));

        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(Instant.now());

        if (request != null) {
            refreshToken.setDeviceInfo(request.getHeader("User-Agent"));
            refreshToken.setIpAddress(request.getRemoteAddr());
        }

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

}
