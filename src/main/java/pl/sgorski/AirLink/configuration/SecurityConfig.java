package pl.sgorski.AirLink.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/", "/docs/**", "/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/graphql", "/graphiql/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/flights/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/flights/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/reservations/restore/**").hasAuthority("ADMIN")
                        .requestMatchers("/actuator/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/airports/**", "/api/airplanes/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/reservations/**").authenticated()
                        .requestMatchers("/api/profile/**").authenticated()
                        .anyRequest().denyAll())
                .userDetailsService(userDetailsService)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler()))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 16, 1, 65536, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }
}
