package com.arishi.AXAM.util;

public final class EmailTemplateUtil {

    private EmailTemplateUtil() {
    }

    public static String verificationEmail(String firstName, String verificationLink) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Email Verification</title>
                </head>
                <body style="font-family: Arial, sans-serif;background:#f5f5f5;padding:40px;">
                
                    <div style="max-width:600px;background:#ffffff;padding:30px;margin:auto;border-radius:8px;">
                
                        <h2>Verify your email</h2>
                
                        <p>Hello %s,</p>
                
                        <p>
                            Thank you for registering.
                            Please verify your email by clicking the button below.
                        </p>
                
                        <p style="text-align:center;margin:30px 0;">
                            <a href="%s"
                               style="
                               background:#2563eb;
                               color:white;
                               text-decoration:none;
                               padding:14px 24px;
                               border-radius:6px;">
                               Verify Email
                            </a>
                        </p>
                
                        <p>
                            If you didn't create this account,
                            you can safely ignore this email.
                        </p>
                
                    </div>
                
                </body>
                </html>
                """.formatted(firstName, verificationLink);
    }

    public static String resetPasswordEmail(String firstName, String resetLink) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Reset Password</title>
                </head>
                <body style="font-family: Arial, sans-serif;background:#f5f5f5;padding:40px;">
                
                    <div style="max-width:600px;background:#ffffff;padding:30px;margin:auto;border-radius:8px;">
                
                        <h2>Reset Password</h2>
                
                        <p>Hello %s,</p>
                
                        <p>
                            Click the button below to reset your password.
                        </p>
                
                        <p style="text-align:center;margin:30px 0;">
                            <a href="%s"
                               style="
                               background:#dc2626;
                               color:white;
                               text-decoration:none;
                               padding:14px 24px;
                               border-radius:6px;">
                               Reset Password
                            </a>
                        </p>
                
                        <p>
                            If you didn't request this,
                            simply ignore this email.
                        </p>
                
                    </div>
                
                </body>
                </html>
                """.formatted(firstName, resetLink);
    }

}
