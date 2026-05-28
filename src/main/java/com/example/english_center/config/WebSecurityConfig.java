package com.example.english_center.config;

import com.example.english_center.security.CustomJwtAuthConverter;
import com.example.english_center.security.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final CustomJwtAuthConverter customJwtAuthConverter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final PasswordEncoder passwordEncoder;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Tắt CSRF tạm thời để test API bằng Postman cho dễ
                .csrf(csrf -> csrf.disable())

                // Phân quyền URL
                .authorizeHttpRequests(request -> request
                        // URL public, không cần đăng nhập
                        .requestMatchers("/",
                                "/security/public",
                                "/auth/login",
                                "/auth/register",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/login**"
                        ).permitAll()

                        // Test role
                        .requestMatchers("/security/manager").hasRole("MANAGER")
                        .requestMatchers("/security/teacher").hasRole("TEACHER")
                        .requestMatchers("/security/student").hasRole("STUDENT")
                        .requestMatchers("/security/staff").hasAnyRole("MANAGER", "TEACHER")

                        // Chỉ cần đăng nhập, role nào cũng được
                        .requestMatchers("/security/auth").authenticated()

                        // Các URL khác trong project tạm thời cho truy cập hết
                        .anyRequest().permitAll()
                )

                // Dùng Basic Auth để test nhanh bằng Postman
                .httpBasic(Customizer.withDefaults())

                // Không lưu session, hợp với hướng REST API/JWT sau này
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthConverter)
                        )
                )
                .authenticationProvider(authenticationProvider())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}