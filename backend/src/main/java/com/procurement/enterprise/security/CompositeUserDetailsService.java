package com.procurement.enterprise.security;

import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.VendorRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Primary
public class CompositeUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    public CompositeUserDetailsService(
            UserRepository userRepository,
            VendorRepository vendorRepository) {

        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        // ----------------------------------------------------
        // 1. Check Employee table
        // ----------------------------------------------------
        Optional<User> employee =
                userRepository.findByEmailAndIsDeletedFalse(normalizedEmail);

        if (employee.isPresent()) {

            User user = employee.get();

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getIsActive(),
                    true,
                    true,
                    true,
                    List.of(new SimpleGrantedAuthority(
                            toAuthority(user.getRole().getName())
                    ))
            );
        }

        // ----------------------------------------------------
        // 2. Check Vendor table
        // ----------------------------------------------------
        Optional<Vendor> vendor =
                vendorRepository.findByEmailAndIsDeletedFalse(normalizedEmail);

        if (vendor.isPresent()) {

            Vendor v = vendor.get();

            return org.springframework.security.core.userdetails.User
                    .withUsername(v.getEmail())
                    .password(v.getPasswordHash())
                    .disabled(!Boolean.TRUE.equals(v.getIsActive()))
                    .authorities(List.of(
                            new SimpleGrantedAuthority("ROLE_VENDOR")
                    ))
                    .build();
        }

        throw new UsernameNotFoundException(
                "No employee or vendor found with email: " + normalizedEmail
        );
    }

    private String toAuthority(String roleName) {

        String normalizedRole = roleName.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedRole) {

            case "ADMIN" ->
                    "ROLE_ADMIN";

            case "DEPARTMENT MANAGER", "MANAGER" ->
                    "ROLE_MANAGER";

            case "EMPLOYEE" ->
                    "ROLE_EMPLOYEE";

            case "FINANCE", "FINANCE OFFICER" ->
                    "ROLE_FINANCE";

            case "PROCUREMENT OFFICER" ->
                    "ROLE_PROCUREMENT_OFFICER";

            case "VENDOR" ->
                    "ROLE_VENDOR";

            default ->
                    "ROLE_" + normalizedRole.replaceAll("\\s+", "_");
        };
    }
}