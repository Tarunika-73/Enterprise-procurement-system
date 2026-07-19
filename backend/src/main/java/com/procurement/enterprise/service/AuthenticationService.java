package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.LoginRequest;
import com.procurement.enterprise.security.JwtAuthenticationResponse;

/** Service interface for Authentication operations. */
public interface AuthenticationService {

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request   login credentials
     * @param ipAddress client IP address for audit
     * @param userAgent client user-agent for audit
     * @return JWT access and refresh tokens with user info
     */
    JwtAuthenticationResponse login(LoginRequest request, String ipAddress, String userAgent);

    /**
     * Refreshes an access token using a valid refresh token.
     *
     * @param refreshToken the refresh token
     * @return new JWT authentication response
     */
    JwtAuthenticationResponse refreshToken(String refreshToken);

    /**
     * Invalidates the user session (logout).
     *
     * @param sessionToken the session token to invalidate
     */
    void logout(String sessionToken);
}
