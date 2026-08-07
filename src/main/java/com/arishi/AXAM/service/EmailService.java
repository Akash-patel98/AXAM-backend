package com.arishi.AXAM.service;


public interface EmailService {

    void sendVerificationEmail(String to, String verificationLink);

     void sendResetPasswordMail(String email, String tokenHash) ;

}
