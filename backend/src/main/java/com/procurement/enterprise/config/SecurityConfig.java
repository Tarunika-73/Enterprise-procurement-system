package com.procurement.enterprise.config;

import com.procurement.enterprise.security.JwtAuthenticationFilter;
import com.procurement.enterprise.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * <ul>
 *   <li>Stateless JWT-based authentication</li>
 *   <li>BCrypt password encoding (bean provided by {@link PasswordConfig})</li>
 *   <li>Role-based endpoint authorization</li>
 *   <li>Public access for auth and Swagger endpoints</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public endpoints ──────────────────────────────────────
                .requestMatchers(
                    "/v1/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()

                // ── Role management — ADMIN only ──────────────────────────
                .requestMatchers("/v1/roles/**")
                    .hasRole(Constants.ROLE_ADMIN)

                // ── User management — ADMIN, MANAGER ─────────────────────
                .requestMatchers("/v1/users/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Department management — ADMIN, MANAGER ────────────────
                .requestMatchers("/v1/departments/**", "/departments/**")
                    .permitAll()
                    
                // ── Vendor management — ADMIN, MANAGER ───────────────────
                .requestMatchers("/v1/vendors/**", "/vendors/**")
                    // .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                    .permitAll()

                // ── Category & Product — ADMIN, MANAGER, EMPLOYEE ────────
                .requestMatchers("/v1/categories/**", "/categories/**")
                    .permitAll()
                .requestMatchers("/v1/supplier-compliance/**", "/supplier-compliance/**")
                    .permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)
                .requestMatchers("/v1/products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Purchase Requests — ADMIN, MANAGER, EMPLOYEE ─────────
                .requestMatchers("/v1/purchase-requests/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)
                .requestMatchers("/v1/purchase-request-items/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)

                // ── Approvals — ADMIN, MANAGER ────────────────────────────
                .requestMatchers("/v1/approvals/**", "/approvals/**")
                    // .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                    .permitAll()
                    
                .requestMatchers("/v1/approval/**", "/approval/**")
                    .permitAll()
                .requestMatchers("/v1/approval-history/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Purchase Orders — ADMIN, MANAGER ─────────────────────
                .requestMatchers("/v1/purchase-orders/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/purchase-order-items/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Purchase Requisitions
                .requestMatchers(
                        "/v1/purchase-requisitions/**",
                        "/purchase-requisitions/**"
                ).permitAll()

                    

                // ── Vendor Estimates — ADMIN, MANAGER ────────────────────
                .requestMatchers("/v1/vendor-estimates/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/vendor-products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Deliveries & Receipts — ADMIN, MANAGER ───────────────
                .requestMatchers("/v1/deliveries/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/receipts/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Finance — ADMIN, FINANCE ──────────────────────────────
                .requestMatchers("/v1/invoices/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_FINANCE)
                .requestMatchers("/v1/payments/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_FINANCE)

                // ── Supplier — ADMIN, MANAGER ─────────────────────────────
                // .requestMatchers("/v1/supplier-performance/**")
                //     .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/supplier-performance/**")
                    .permitAll()

                // .requestMatchers("/api/v1/supplier-performance/**")
                //     .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // ── Audit & Sessions — ADMIN only ─────────────────────────
                .requestMatchers("/v1/audit-logs/**")
                    .hasRole(Constants.ROLE_ADMIN)
                .requestMatchers("/v1/login-history/**")
                    .hasRole(Constants.ROLE_ADMIN)
                .requestMatchers("/v1/user-sessions/**")
                    .hasRole(Constants.ROLE_ADMIN)

                // ── Notifications — any authenticated user ────────────────
                .requestMatchers("/v1/notifications/**")
                    .authenticated()

                // ── Catch-all ─────────────────────────────────────────────
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
