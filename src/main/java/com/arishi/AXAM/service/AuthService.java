package com.arishi.AXAM.service;


import com.arishi.AXAM.dto.request.*;
import com.arishi.AXAM.dto.responce.LoginResult;
import com.arishi.AXAM.dto.responce.RegistrationResponse;
import com.arishi.AXAM.dto.responce.TokenResponse;

public interface AuthService {


    RegistrationResponse signup(RegistrationRequest request);

    LoginResult login(LoginRequest request);

    void verifyEmail(String token);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);


}
