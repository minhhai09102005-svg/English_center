package com.example.english_center.security;

import com.example.english_center.entity.Users;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsersRepository usersRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Long userId;

        try {
            userId = Long.valueOf(jwt.getSubject());
        } catch (Exception e) {
            throw new BadCredentialsException("JWT subject không hợp lệ");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("Tài khoản đã bị khóa hoặc chưa kích hoạt");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                jwt,
                userPrincipal.getAuthorities()
        );
    }
}