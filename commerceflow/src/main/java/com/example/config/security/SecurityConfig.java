package com.example.config.security;

import com.example.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    public SecurityConfig() {
        System.out.println(">>> SecurityConfig CREATED");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {

        http
                // disable CSRF for stateless APIs
                .csrf(csrf -> csrf.disable())

                // enable CORS (configure elsewhere if needed)
                .cors(cors -> {})

                // stateless session management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // handle unauthenticated requests with 401
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
                )

                .authorizeHttpRequests(auth -> auth
                        // ============================
                        // SWAGGER / OPENAPI + static assets
                        // ============================
                		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()


                        // ============================
                        // PUBLIC AUTH ENDPOINTS
                        // ============================
                        .requestMatchers("/auth/login", "/auth/validate").permitAll()

                        // ============================
                        // PUBLIC CUSTOMER ENDPOINTS
                        // ============================
                        .requestMatchers(HttpMethod.GET, "/api/customers/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").permitAll()

                        // ============================
                        // PROTECTED DELETE ENDPOINT
                        // ============================
                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**").authenticated()

                        // everything else → public (keeps previous behavior)
                        .anyRequest().permitAll()
                )

                // Add JWT filter before the standard username/password filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
