package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.service.EmailService;
import com.arishi.AXAM.util.EmailTemplateUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendVerificationEmail(String to, String verificationLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = EmailTemplateUtil.verificationEmail("User", verificationLink);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("verify your email");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception ex) {

            throw new RuntimeException("failed to send verification email", ex);
        }
    }

    @Async
    @Override
    public void sendResetPasswordMail(String email, String resetLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = EmailTemplateUtil.resetPasswordEmail("User", resetLink);

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("reset Your password");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception ex) {

            throw new RuntimeException("Failed to send reset password email", ex);
        }
    }
}