package com.procurement.enterprise.security;

import lombok.Builder;
import lombok.Getter;

/**
 * Response payload returned after a successful authentication.
 * Contains the JWT access token, refresh token, and basic user information.
 */
@Getter
@Builder
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private Long departmentId;
    private String departmentName;
}
