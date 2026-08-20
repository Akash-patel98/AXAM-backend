package com.arishi.AXAM.service;


import com.arishi.AXAM.dto.request.*;
import com.arishi.AXAM.dto.responce.LoginResult;
import com.arishi.AXAM.dto.responce.RegistrationResponse;
import com.arishi.AXAM.dto.responce.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

public interface AuthService {


    RegistrationResponse signup(RegistrationRequest request);

    LoginResult login(LoginRequest request);

    void verifyEmail(String token);

    void forgotPassword(ForgotPasswordRequest request);

    @Transactional
    void logout(HttpServletRequest request, HttpServletResponse response);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    String refreshAccessToken(String refreshToken);

    void resendVerification(@Valid ResendVerificationRequest request);

}
