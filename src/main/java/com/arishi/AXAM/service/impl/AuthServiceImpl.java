package com.arishi.AXAM.service.impl;


import com.arishi.AXAM.dto.request.*;
import com.arishi.AXAM.dto.responce.LoginResponse;
import com.arishi.AXAM.dto.responce.LoginResult;
import com.arishi.AXAM.dto.responce.RegistrationResponse;
import com.arishi.AXAM.dto.responce.TokenResponse;
import com.arishi.AXAM.enums.UserStatus;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.InvalidRefreshTokenException;
import com.arishi.AXAM.exception.InvalidTokenException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.UserMapper;
import com.arishi.AXAM.model.*;
import com.arishi.AXAM.repo.*;
import com.arishi.AXAM.security.CustomUserDetails;
import com.arishi.AXAM.security.JwtService;
import com.arishi.AXAM.security.RefreshTokenIssuer;
import com.arishi.AXAM.service.AuthService;
import com.arishi.AXAM.service.EmailService;
import com.arishi.AXAM.util.CookieUtils;
import com.arishi.AXAM.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenIssuer refreshTokenIssuer;

    private final HashUtil hashUtil;

    private final EmailVerificationTokenRepository emailTokenRepository;

    private final PasswordResetTokenRepository passwordResetRepository;

    private final LoginHistoryRepository loginHistoryRepository;


    private final EmailService emailService;

    @Override
    @Transactional
    public RegistrationResponse signup(RegistrationRequest request) {

        //  email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Convert DTO to Entity
        Users user = userMapper.toEntity(request);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password not match.");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Default role Candidate
        Roles role = roleRepository.findByName("CANDIDATE").orElseThrow(() -> new ResourceNotFoundException("Default role CANDIDATE not found"));

        user.setRole(role);

        user.setStatus(UserStatus.PENDING);

        userRepository.save(user);

        // verification token genrate
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        String rawToken = UUID.randomUUID().toString();
        token.setTokenHash(hashUtil.sha256(rawToken));
        token.setExpiresAt(Instant.now().plusSeconds(120));

        emailTokenRepository.save(token);

        // create verification link
        String verificationLink = frontendUrl + "/verifyemail?token=" + rawToken;

        // Send email
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);


        return RegistrationResponse.builder().userId(user.getId()).email(user.getEmail()).message("Verification email sent").build();
    }


    @Override
    public LoginResult login(LoginRequest request) {

        Users user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElse(null);
        if (user == null) {
            throw new BadRequestException("Invalid email or password");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            recordLoginHistory(user, "PASSWORD", "FAILURE", "Invalid password");
            throw new BadRequestException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            recordLoginHistory(user, "PASSWORD", "FAILURE", "Account blocked");
            throw new BadRequestException("Account is blocked");
        }

        if (user.getStatus() == UserStatus.PENDING || !user.isEmailVerified()) {
            recordLoginHistory(user, "PASSWORD", "FAILURE", "email not verified");
            throw new BadRequestException("verify your email before logging");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenIssuer.issue(user, null);

        LoginResponse response = LoginResponse.builder().userId(user.getId()).email(user.getEmail()).role(user.getRole().getName()).fristName(user.getFirstName()).lastName(user.getLastName()).build();

        return LoginResult.builder().response(response).accessToken(accessToken).refreshToken(refreshToken).build();

    }


    @Transactional
    @Override
    public void verifyEmail(String tokenHash) {

        EmailVerificationToken verificationToken = emailTokenRepository.findByTokenHash(hashUtil.sha256(tokenHash)).orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.isUsed()) throw new InvalidTokenException("Verification token already used");

        if (verificationToken.getExpiresAt().isBefore(Instant.now()))
            throw new InvalidTokenException("Verification token expired");

        Users user = verificationToken.getUser();

        user.setEmailVerified(true);

        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        verificationToken.setUsed(true);

        verificationToken.setVerifiedAt(Instant.now());

        emailTokenRepository.save(verificationToken);

    }


    private void recordLoginHistory(Users user, String loginType, String status, String failureReason) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setLoginType(loginType);
        history.setLoginStatus(status);
        history.setFailureReason(failureReason);
        history.setLoginTime(Instant.now());
        loginHistoryRepository.save(history);
    }


    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        Users user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElse(null);
        if (user == null) {
            return;
        }

        PasswordResetToken token = new PasswordResetToken();

        token.setUser(user);
        String rawToken = UUID.randomUUID().toString();
        token.setTokenHash(hashUtil.sha256(rawToken));
        token.setExpiresAt(Instant.now().plusSeconds(15 * 60));

        passwordResetRepository.save(token);

        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;

        emailService.sendResetPasswordMail(user.getEmail(), user.getFirstName(), resetLink);

    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // Get refresh token
        String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");

        // Revoke refresh token
        if (refreshToken != null) {
            refreshTokenRepository.findByTokenHash(hashUtil.sha256(refreshToken)).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }

        // Delete cookies
        CookieUtils.deleteCookie(response, "accessToken", "/");
        CookieUtils.deleteCookie(response, "refreshToken", "/api/auth");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetRepository.findByTokenHash(hashUtil.sha256(request.getToken())).orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isUsed()) throw new InvalidTokenException("Reset token already used");

        if (resetToken.getExpiresAt().isBefore(Instant.now())) throw new InvalidTokenException("Reset token expired");

        Users user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetRepository.save(resetToken);

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication = getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            throw new BadRequestException("Unexpected principal");
        }

        Users user = customUserDetails.getUser();

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New passwords do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByTokenHash(refreshToken).orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.isRevoked()) throw new InvalidRefreshTokenException("Refresh token has been revoked");


        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new InvalidRefreshTokenException("Refresh token has expired");

        Users user = token.getUser();

        return jwtService.generateAccessToken(user);

    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {

        Users user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (user.isEmailVerified() || user.getStatus() == UserStatus.ACTIVE) {

            throw new BadRequestException("Email is already verified");
        }

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        String rawToken = UUID.randomUUID().toString();
        token.setTokenHash(hashUtil.sha256(rawToken));
        token.setExpiresAt(Instant.now().plusSeconds(120));

        emailTokenRepository.save(token);

        String verificationLink = frontendUrl + "/verifyemail?token=" + rawToken;

        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);
    }

}
