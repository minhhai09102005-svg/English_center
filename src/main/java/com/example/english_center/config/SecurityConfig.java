package com.example.english_center.config;

import com.example.english_center.security.CustomUserDetailsService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Tắt CSRF tạm thời để test API bằng Postman cho dễ
                .csrf(csrf -> csrf.disable())

                // Phân quyền URL
                .authorizeHttpRequests(request -> request
                        // URL public, không cần đăng nhập
                        .requestMatchers("/", "/security/public").permitAll()

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
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
}