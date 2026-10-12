package com.example.campushire.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/companies/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/jobs/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/drives/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/practice/questions/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/practice/mock-tests/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/students/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/companies/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/drives/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/jobs/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/applications/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/practice/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/resumes/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/notifications/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/experiences/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/reviews/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/ai/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/mock-interviews/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/api/study-plan/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*", "https://*.onrender.com", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
