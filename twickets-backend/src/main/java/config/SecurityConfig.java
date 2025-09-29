package config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Testing endpoints
                        .requestMatchers("/api/test/public").permitAll()
                        .requestMatchers("/api/test/user").authenticated()
                        .requestMatchers("/api/test/organizer").hasAnyAuthority("ORGANIZER", "ADMIN")
                        .requestMatchers("/api/test/admin").hasAuthority("ADMIN")

                        // User endpoints - all authenticated users can access
                        .requestMatchers("/api/users/**", "/api/events/view/**", "/api/tickets/purchase/**").authenticated()

                        // Organizer endpoints
                        .requestMatchers("/api/events/create/**", "/api/events/update/**").hasAnyAuthority("ORGANIZER", "ADMIN")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // By default, require authentication for any other endpoint
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}