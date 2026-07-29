package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.LoginRequest;
import com.procurement.enterprise.dto.request.RegisterRequest;
import com.procurement.enterprise.security.JwtAuthenticationResponse;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final com.procurement.enterprise.service.AuthenticationService authenticationService;

    public AuthController(com.procurement.enterprise.service.AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        JwtAuthenticationResponse response = authenticationService.register(request);
        Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", response.getUserId());
        userMap.put("email", response.getEmail());
        userMap.put("fullName", response.getFullName());
        userMap.put("role", response.getRole());
        userMap.put("departmentId", response.getDepartmentId());
        userMap.put("departmentName", response.getDepartmentName());
        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("user", userMap);
        payload.put("token", response.getAccessToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", payload, HttpStatus.CREATED));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request,
                                                                  HttpServletRequest servletRequest) {
        JwtAuthenticationResponse response = authenticationService.login(
                request,
                servletRequest.getRemoteAddr(),
                servletRequest.getHeader("User-Agent")
        );
        Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", response.getUserId());
        userMap.put("email", response.getEmail());
        userMap.put("fullName", response.getFullName());
        userMap.put("role", response.getRole());
        userMap.put("departmentId", response.getDepartmentId());
        userMap.put("departmentName", response.getDepartmentName());
        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("user", userMap);
        payload.put("token", response.getAccessToken());
        payload.put("refreshToken", response.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Login successful", payload));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmail(@RequestParam String email) {
        boolean exists = authenticationService.emailExists(email);
        return ResponseEntity.ok(ApiResponse.success("Email check completed", Map.of("exists", exists)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required", HttpStatus.UNAUTHORIZED));
        }
        return ResponseEntity.ok(ApiResponse.success("Authenticated user", Map.of("email", authentication.getName())));
    }
}
