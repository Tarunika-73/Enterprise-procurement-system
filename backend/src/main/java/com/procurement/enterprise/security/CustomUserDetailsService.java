package com.procurement.enterprise.security;

import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Custom implementation of {@link UserDetailsService} that loads users by email.
 * Maps the user's role to a Spring Security {@link SimpleGrantedAuthority}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address.
     *
     * @param email the user's email (used as the username in this system)
     * @return a fully populated {@link UserDetails} object
     * @throws UsernameNotFoundException if no active user exists with the given email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.getIsActive(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(toAuthority(user.getRole().getName())))
        );
    }

    private String toAuthority(String roleName) {
        String normalizedRole = roleName.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedRole) {
            case "ADMIN" -> "ROLE_ADMIN";
            case "DEPARTMENT MANAGER", "MANAGER" -> "ROLE_MANAGER";
            case "EMPLOYEE" -> "ROLE_EMPLOYEE";
            case "FINANCE OFFICER", "FINANCE" -> "ROLE_FINANCE";
            case "VENDOR" -> "ROLE_VENDOR";
            default -> "ROLE_" + normalizedRole.replaceAll("\\s+", "_");
        };
    }
}
