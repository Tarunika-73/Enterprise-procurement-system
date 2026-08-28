package com.procurement.enterprise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.enterprise.dto.request.RegisterRequest;
import com.procurement.enterprise.security.JwtAuthenticationResponse;
import com.procurement.enterprise.security.JwtUtil;
import com.procurement.enterprise.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class MockMvcConfiguration {

        @Bean
        MockMvcBuilderCustomizer apiContextPathCustomizer() {
            return builder -> builder.defaultRequest(get("/").contextPath("/api"));
        }
    }

    @Test
    void registerShouldReturnCreatedResponse() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("Password123")
                .role("Employee")
                .departmentId(1L)
                .build();

        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .accessToken("test-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .userId(1L)
                .email("jane@example.com")
                .fullName("Jane Doe")
                .role("Employee")
                .departmentId(1L)
                .departmentName("IT")
                .build();

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").value("test-token"));
    }
}
