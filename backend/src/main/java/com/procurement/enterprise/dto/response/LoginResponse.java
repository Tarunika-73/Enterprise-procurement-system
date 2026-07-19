package com.procurement.enterprise.dto.response;

import lombok.*;

@Getter @Builder
public class LoginResponse {
    private String accessToken; private String refreshToken; private String tokenType; private long expiresIn;
    private Long userId; private String email; private String fullName; private String role;
    private Long departmentId; private String departmentName;
}
