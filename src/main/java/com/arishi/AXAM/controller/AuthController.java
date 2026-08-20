package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.*;
import com.arishi.AXAM.dto.responce.LoginResponse;
import com.arishi.AXAM.dto.responce.LoginResult;
import com.arishi.AXAM.dto.responce.RegistrationResponse;
import com.arishi.AXAM.security.ResponseCookieHelper;
import com.arishi.AXAM.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<RegistrationResponse>> signup(@Valid @RequestBody RegistrationRequest request) {

        RegistrationResponse response = authService.signup(request);


        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Signup successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        LoginResult result = authService.login(request);

        ResponseCookieHelper.attachAccessTokenCookie(response, result.getAccessToken());
        ResponseCookieHelper.attachRefreshTokenCookie(response, result.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", result.getResponse()));
    }


    @GetMapping("/verifyemail")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {

        authService.verifyEmail(token);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Email verified successfully"));
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);


        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Password reset link sent"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "logout successfully"));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        ResponseCookieHelper.attachAccessTokenCookie(response, newAccessToken);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Access token refreshed successfully"));
    }

    @PostMapping("/resendverification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {

        authService.resendVerification(request);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Verification email sent successfully"));
    }
}