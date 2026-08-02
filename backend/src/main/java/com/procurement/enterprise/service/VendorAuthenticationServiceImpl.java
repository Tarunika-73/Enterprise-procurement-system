package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.VendorLoginRequest;
import com.procurement.enterprise.dto.request.VendorRegisterRequest;
import com.procurement.enterprise.dto.response.VendorRegisterResponse;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.repository.VendorRepository;
import com.procurement.enterprise.security.JwtUtil;
import com.procurement.enterprise.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VendorAuthenticationServiceImpl implements VendorAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(VendorAuthenticationServiceImpl.class);

    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> login(VendorLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // 1. Look up vendor in vendors table only
        Vendor vendor = vendorRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // 2. Check account is active
        if (!Boolean.TRUE.equals(vendor.getIsActive())) {
            log.warn("Disabled vendor login attempt: {}", email);
            throw new DisabledException("Account is disabled. Contact administrator.");
        }

        // 3. Verify password against BCrypt hash stored in vendors table
        // if (!passwordEncoder.matches(request.getPassword(), vendor.getPasswordHash())) {
        //     throw new BadCredentialsException("Invalid email or password");
        // }

        System.out.println("========== Vendor Login ==========");
        System.out.println("Email            : " + vendor.getEmail());
        System.out.println("Entered Password : " + request.getPassword());
        System.out.println("Stored Hash      : " + vendor.getPasswordHash());

        boolean match = passwordEncoder.matches(
                request.getPassword(),
                vendor.getPasswordHash());

        System.out.println("Password Match : " + match);

        if (!match) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // 4. Build a UserDetails with ROLE_VENDOR so JwtUtil can generate a token
        UserDetails userDetails = User.withUsername(vendor.getEmail())
                .password(vendor.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_VENDOR")))
                .build();

        String accessToken  = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 5. Build vendor info map matching the required response shape
        Map<String, Object> vendorInfo = new LinkedHashMap<>();
        vendorInfo.put("id",          vendor.getId());
        vendorInfo.put("vendorName",  vendor.getVendorName());
        vendorInfo.put("contactName", vendor.getContactName());
        vendorInfo.put("email",       vendor.getEmail());
        vendorInfo.put("role",        "Vendor");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("token",        accessToken);
        payload.put("refreshToken", refreshToken);
        payload.put("tokenType",    Constants.BEARER_PREFIX.trim());
        payload.put("expiresIn",    86400000L);
        payload.put("vendor",       vendorInfo);

        log.info("Vendor login successful: {}", email);
        return payload;
    }

    @Override
    @Transactional
    public VendorRegisterResponse register(VendorRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (vendorRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new DuplicateResourceException("Vendor", "email", email);
        }

        if (vendorRepository.existsByGstNumberAndIsDeletedFalse(request.getGstNumber().trim())) {
            throw new DuplicateResourceException("Vendor", "GST number", request.getGstNumber().trim());
        }

        Vendor vendor = Vendor.builder()
                .vendorName(request.getVendorName().trim())
                .contactName(request.getContactName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone().trim())
                .address(request.getAddress().trim())
                .gstNumber(request.getGstNumber().trim())
                .isActive(true)
                .isDeleted(false)
                .build();

        Vendor saved = vendorRepository.save(vendor);
        log.info("Vendor registered successfully: {}", email);
        return new VendorRegisterResponse(saved.getId(), saved.getVendorName(), saved.getContactName(), saved.getEmail());
    }
}
