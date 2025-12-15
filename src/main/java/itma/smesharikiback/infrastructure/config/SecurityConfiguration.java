package itma.smesharikiback.infrastructure.config;


import itma.smesharikiback.application.service.CommonService;
import itma.smesharikiback.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] MANAGEMENT_ENDPOINTS = {
            "/actuator/health", "/actuator/health/**",
            "/actuator/info",
            "/actuator/metrics", "/actuator/metrics/**",
            "/actuator/prometheus"
    };
    private static final List<String> DEV_ALLOWED_ORIGINS = List.of(
            "http://localhost",
            "http://localhost:4200",
            "http://localhost:80"
    );

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CommonService commonService;
    private final Environment environment;
    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(MANAGEMENT_ENDPOINTS).permitAll()
                        .requestMatchers("/api/**").hasAnyAuthority("ADMIN", "USER", "DOCTOR")
                        .requestMatchers("/ws/**").hasAnyAuthority("ADMIN", "USER", "DOCTOR")
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(commonService::getByLogin);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = resolveAllowedOrigins();
        validateOriginsForCredentials(origins, Boolean.TRUE);
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> resolveAllowedOrigins() {
        boolean prodProfile = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (prodProfile) {
            List<String> origins = parseOrigins(allowedOrigins);
            if (origins.isEmpty()) {
                throw new IllegalStateException("Configure app.cors.allowed-origins for prod profile");
            }
            return origins;
        }
        return DEV_ALLOWED_ORIGINS;
    }

    private List<String> parseOrigins(String originsSource) {
        if (originsSource == null) {
            return Collections.emptyList();
        }
        String sanitized = originsSource
                .replace("[", "")
                .replace("]", "");
        return Arrays.stream(sanitized.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void validateOriginsForCredentials(List<String> origins, Boolean allowCredentials) {
        if (Boolean.TRUE.equals(allowCredentials) && origins.stream().anyMatch("*"::equals)) {
            throw new IllegalStateException("Wildcard origins are not allowed when credentials are enabled");
        }
    }
}
