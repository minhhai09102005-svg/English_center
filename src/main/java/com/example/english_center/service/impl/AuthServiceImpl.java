package com.example.english_center.service.impl;

import com.example.english_center.customExceptions.AccountLockedException;
import com.example.english_center.customExceptions.DataNotFoundException;
import com.example.english_center.customExceptions.InvalidLoginException;
import com.example.english_center.dto.request.LoginRequest;
import com.example.english_center.entity.Users;
import com.example.english_center.repository.UsersRepository;
import com.example.english_center.service.AuthService;
import com.example.english_center.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public String login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException | LockedException e) {
            throw AccountLockedException.locked();
        } catch (BadCredentialsException e) {
            throw InvalidLoginException.invalidCredentials();
        }

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(DataNotFoundException::user);

        return jwtTokenProvider.generateToken(user);
    }
}
