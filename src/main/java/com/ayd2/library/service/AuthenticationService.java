package com.ayd2.library.service;

import com.ayd2.library.dto.AuthRequest;
import com.ayd2.library.dto.JwtRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtRequest createToken(AuthRequest reqDto) throws LibraryException {
        var authData = new UsernamePasswordAuthenticationToken(reqDto.username(), reqDto.password());

        try {
            var authentication = authenticationManager.authenticate(authData);
            if (authentication.isAuthenticated()) {
                var userDetails = userDetailsService.loadUserByUsername(reqDto.username());
                var token = jwtService.generateToken(userDetails);
                return new JwtRequest(token);
            }
        } catch (IOException e) {
            log.error("Error:", e);
        }

        throw new LibraryException("invalid_user");
    }
}
