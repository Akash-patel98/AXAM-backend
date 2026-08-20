package com.arishi.AXAM.security;

import com.arishi.AXAM.enums.UserStatus;
import com.arishi.AXAM.model.LoginHistory;
import com.arishi.AXAM.model.OAuthAccount;
import com.arishi.AXAM.model.Roles;
import com.arishi.AXAM.model.Users;
import com.arishi.AXAM.repo.LoginHistoryRepository;
import com.arishi.AXAM.repo.OAuthAccountRepository;
import com.arishi.AXAM.repo.RoleRepository;
import com.arishi.AXAM.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Transactional
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final JwtService jwtService;
    private final RefreshTokenIssuer refreshTokenIssuer;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // Get authenticated Google user
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Get OAuth provider
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // Get Google user information
        String email = oAuth2User.getAttribute("email");
        String providerUserId = oAuth2User.getName();
        String name = oAuth2User.getAttribute("name");

        // Find existing user or create/link OAuth account
        Users user = resolveUser(provider, providerUserId, email, name);

        // Record successful OAuth login
        recordLoginHistory(user, provider, request);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user);

        String rawRefreshToken = refreshTokenIssuer.issue(user, request);

        // Store tokens in HttpOnly cookies
        ResponseCookieHelper.attachAccessTokenCookie(response, accessToken);

        ResponseCookieHelper.attachRefreshTokenCookie(response, rawRefreshToken);

        // Redirect to frontend
        response.sendRedirect(frontendUrl + "/oauth2/redirect");
    }


    //  Find OAuth account.
    private Users resolveUser(String provider, String providerUserId, String email, String name) {

        return oAuthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId).map(OAuthAccount::getUser).orElseGet(() -> linkOrCreateUser(provider, providerUserId, email, name));
    }

    // Link OAuth account with existing user or create a new user.

    private Users linkOrCreateUser(String provider, String providerUserId, String email, String name) {

        Users user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseGet(() -> createUser(email, name));

        OAuthAccount account = new OAuthAccount();

        account.setUser(user);
        account.setProvider(provider);
        account.setProviderUserId(providerUserId);
        account.setProviderEmail(email);

        oAuthAccountRepository.save(account);

        return user;
    }

    // Create a new user for Google OAuth login.
    private Users createUser(String email, String name) {

        Roles defaultRole = roleRepository.findByName("CANDIDATE").orElseThrow(() -> new IllegalStateException("Default role Candidate not set"));

        String firstName = name;
        String lastName = "";

        if (name != null && name.contains(" ")) {

            int index = name.indexOf(" ");

            firstName = name.substring(0, index);
            lastName = name.substring(index + 1);
        }

        Users user = new Users();

        user.setEmail(email);

        user.setFirstName(firstName != null ? firstName : email);

        user.setLastName(lastName);

        user.setRole(defaultRole);

        // Google has already verified the email
        user.setEmailVerified(true);

        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    private void recordLoginHistory(Users user, String provider, HttpServletRequest request) {

        LoginHistory history = new LoginHistory();

        history.setUser(user);

        history.setLoginType("OAUTH2");

        history.setProvider(provider);

        history.setIpAddress(request.getRemoteAddr());

        history.setDeviceInfo(request.getHeader("User-Agent"));

        history.setLoginStatus("SUCCESS");

        history.setLoginTime(Instant.now());

        loginHistoryRepository.save(history);
    }
}