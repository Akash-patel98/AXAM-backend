package com.arishi.AXAM.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@NoArgsConstructor
public final class ResponseCookieHelper {


    public static void attachRefreshTokenCookie(HttpServletResponse response, String rawRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", rawRefreshToken).httpOnly(true).secure(false).path("/api/auth").maxAge(Duration.ofDays(30)).sameSite("Strict").build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void attachAccessTokenCookie(HttpServletResponse response, String accessToken) {

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken).httpOnly(true).secure(false).sameSite("Strict").path("/").maxAge(Duration.ofMinutes(15)).build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


}
