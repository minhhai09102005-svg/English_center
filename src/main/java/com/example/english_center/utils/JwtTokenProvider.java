package com.example.english_center.utils;

import com.example.english_center.entity.Users;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecretKey jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Users user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                // Subject là userId, giống cách làm của project nhà hàng
                .subject(user.getId().toString())

                // Thêm email và role vào token để debug/đọc dễ hơn
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())

                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }
}