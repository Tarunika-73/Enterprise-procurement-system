package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.RegisterRequest;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Role;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.RoleRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void registerShouldAcceptManagerRoleAlias() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Jane Manager")
                .email("manager@example.com")
                .password("Password123")
                .role("Manager")
                .departmentId(2L)
                .build();

        Role managerRole = Role.builder().id(2L).name("Department Manager").build();
        Department department = Department.builder().id(2L).name("Human Resources").code("HR").build();

        when(userRepository.existsByEmailAndIsDeletedFalse("manager@example.com")).thenReturn(false);
        when(roleRepository.findByNameAndIsDeletedFalse("Department Manager")).thenReturn(Optional.empty());
        when(roleRepository.findAllByIsDeletedFalse()).thenReturn(java.util.List.of(managerRole));
        when(departmentRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });
        when(jwtUtil.generateToken(any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");

        var response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("Department Manager", response.getRole());
        assertEquals(2L, response.getDepartmentId());
        assertEquals("Human Resources", response.getDepartmentName());
    }
}
