package com.procurement.enterprise.config;

import com.procurement.enterprise.security.CompositeUserDetailsService;
import com.procurement.enterprise.security.JwtAuthenticationFilter;
import com.procurement.enterprise.security.CustomUserDetailsService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomUserDetailsService employeeUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomUserDetailsService employeeUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.employeeUserDetailsService = employeeUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Preflight & public auth endpoints
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/auth/**",
                    "/vendor/auth/**",
                    "/health",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()

                // Admin module - ADMIN only
                .requestMatchers("/v1/admin/**")
                    .hasRole(Constants.ROLE_ADMIN)

                // Role management - ADMIN only
                .requestMatchers("/v1/roles/**")
                    .hasRole(Constants.ROLE_ADMIN)

                // User management - ADMIN, MANAGER
                .requestMatchers("/v1/users/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Department management
                .requestMatchers("/v1/departments/**")
                    .permitAll()

                // Vendor portal (vendor-facing)
                .requestMatchers("/v1/vendor-portal/**")
                    .hasRole(Constants.ROLE_VENDOR)

                // Procurement Officer module
                .requestMatchers("/v1/procurement/**")
                    .hasAnyRole(Constants.ROLE_PROCUREMENT_OFFICER, Constants.ROLE_ADMIN)

                // Vendor management
                .requestMatchers("/v1/vendors/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Categories
                .requestMatchers("/v1/categories/**")
                    .permitAll()

                // Supplier compliance
                .requestMatchers("/v1/supplier-compliance/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Products
                .requestMatchers(HttpMethod.GET, "/v1/products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)
                .requestMatchers("/v1/products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Purchase Requests
                .requestMatchers("/v1/purchase-requests/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)
                .requestMatchers("/v1/purchase-request-items/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)

                // Approvals
                .requestMatchers("/v1/approvals/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/approval-history/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/approval-workflow/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Purchase Requisitions
                .requestMatchers("/v1/purchase-requisitions/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_EMPLOYEE)

                // Purchase Orders
                .requestMatchers("/v1/purchase-orders/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/purchase-order-items/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Vendor Estimates & Products
                .requestMatchers("/v1/vendor-estimates/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/vendor-products/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Deliveries & Receipts
                .requestMatchers("/v1/deliveries/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)
                .requestMatchers("/v1/receipts/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_PROCUREMENT_OFFICER)

                // Finance
                .requestMatchers("/v1/finance/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_FINANCE)
                .requestMatchers("/v1/invoices/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_FINANCE)
                .requestMatchers("/v1/payments/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_FINANCE)

                // Supplier Performance
                .requestMatchers("/v1/supplier-performance/**")
                    .hasAnyRole(Constants.ROLE_ADMIN, Constants.ROLE_MANAGER)

                // Audit & Sessions - ADMIN only
                .requestMatchers("/v1/audit-logs/**")
                    .hasRole(Constants.ROLE_ADMIN)
                .requestMatchers("/v1/login-history/**")
                    .hasRole(Constants.ROLE_ADMIN)
                .requestMatchers("/v1/user-sessions/**")
                    .hasRole(Constants.ROLE_ADMIN)

                // Notifications - any authenticated user
                .requestMatchers("/v1/notifications/**")
                    .authenticated()

                // Catch-all
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(employeeUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
