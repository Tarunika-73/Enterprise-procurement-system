package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.LoginRequest;
import com.procurement.enterprise.dto.request.RegisterRequest;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Role;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.RoleRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.security.JwtAuthenticationResponse;
import com.procurement.enterprise.security.JwtUtil;
import com.procurement.enterprise.util.Constants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     DepartmentRepository departmentRepository,
                                     PasswordEncoder passwordEncoder,
                                     JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Role role = resolveRole(request.getRole());

        if (request.getDepartmentId() == null) {
            throw new InvalidRequestException("Department is required");
        }

        Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                .orElseThrow(() -> new InvalidRequestException("Invalid department selected"));

        User user = User.builder()
                .employeeId(generateEmployeeId())
                .firstName(extractFirstName(request.getFullName()))
                .lastName(extractLastName(request.getFullName()))
                .email(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .department(department)
                .isActive(true)
                .isDeleted(false)
                .build();

        user = userRepository.save(user);

        // If a manager registers and the department has no manager, assign them.
        if (isManagerRole(role.getName()) && department.getManager() == null) {
            department.setManager(user);
            departmentRepository.save(department);
        }

        String token = jwtUtil.generateToken(org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + role.getName().toUpperCase(Locale.ROOT).replace(" ", "_"))
                .build());

        return JwtAuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(jwtUtil.generateRefreshToken(org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities("ROLE_" + role.getName().toUpperCase(Locale.ROOT).replace(" ", "_"))
                        .build()))
                .tokenType(Constants.BEARER_PREFIX.trim())
                .expiresIn(86400000)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(role.getName())
                .employeeId(user.getEmployeeId())
                .departmentId(department.getId())
                .departmentName(department.getName())
                .build();
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse login(LoginRequest request, String ipAddress, String userAgent) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(Locale.ROOT), request.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new InvalidRequestException("Account is disabled");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().getName().toUpperCase(Locale.ROOT).replace(" ", "_"))
                .build();

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(Constants.BEARER_PREFIX.trim())
                .expiresIn(86400000)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole().getName())
                .employeeId(user.getEmployeeId())
                .departmentId(user.getDepartment().getId())
                .departmentName(user.getDepartment().getName())
                .build();
    }

    @Override
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        throw new UnsupportedOperationException("Refresh token not implemented yet");
    }

    @Override
    public void logout(String sessionToken) {
        // No-op for now; session persistence omitted.
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmailAndIsDeletedFalse(email.trim().toLowerCase(Locale.ROOT));
    }

    private Role resolveRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return roleRepository.findByNameAndIsDeletedFalse("Employee")
                    .orElseThrow(() -> new InvalidRequestException("Invalid role provided"));
        }

        String normalized = normalizeRoleName(roleName);
        Optional<Role> exactMatch = roleRepository.findByNameAndIsDeletedFalse(normalized);
        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        return roleRepository.findAllByIsDeletedFalse().stream()
                .filter(role -> roleMatches(role.getName(), normalized))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Invalid role provided"));
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return "Employee";
        }

        String trimmed = roleName.trim();
        Map<String, String> roleAliases = Map.of(
                "admin", "Admin",
                "employee", "Employee",
                "manager", "Department Manager",
                "department manager", "Department Manager",
                "finance", "Finance Officer",
                "finance officer", "Finance Officer",
                "procurement officer", "Procurement Officer",
                "vendor", "Vendor"
        );

        return roleAliases.getOrDefault(trimmed.toLowerCase(Locale.ROOT), trimmed);
    }

    private boolean roleMatches(String storedRoleName, String requestedRoleName) {
        String normalizedStored = normalizeRoleName(storedRoleName);
        String normalizedRequested = normalizeRoleName(requestedRoleName);
        return normalizedStored.equalsIgnoreCase(normalizedRequested);
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "User";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "User";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : "User";
    }

    private String generateEmployeeId() {
        long count = userRepository.count();
        return "EMP" + String.format("%03d", count + 1);
    }

    private boolean isManagerRole(String roleName) {
        if (roleName == null) return false;
        String normalized = roleName.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("manager") || normalized.equals("department manager");
    }
}
