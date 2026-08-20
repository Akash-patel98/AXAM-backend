package com.arishi.AXAM.service;


public interface EmailService {

    void sendVerificationEmail(String to, String firstName, String verificationLink);

    void sendResetPasswordMail(String email, String firstName, String resetLink);

}
