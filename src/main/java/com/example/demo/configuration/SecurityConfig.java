package com.example.demo.configuration;

import com.example.demo.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())  // Enable CORS configuration (applies the CORS settings from WebConfig)
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection since JWT tokens handle it
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/register", "/api/login", "/api/admin/registerAdmin").permitAll()  // Public endpoints
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow all preflight requests (OPTIONS)
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Allow access to Swagger UI and OpenAPI docs
                                .anyRequest().authenticated()  // All other endpoints require authentication
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session (JWT)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }


}
