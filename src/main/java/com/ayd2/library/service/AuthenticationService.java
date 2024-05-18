package com.ayd2.library.service;

import com.ayd2.library.dto.AuthReqDto;
import com.ayd2.library.dto.JwtResDto;
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

    public JwtResDto createToken(AuthReqDto reqDto) throws LibraryException {
        var authData = new UsernamePasswordAuthenticationToken(reqDto.username(), reqDto.password());

        try {
            var authentication = authenticationManager.authenticate(authData);
            if (authentication.isAuthenticated()) {
                var userDetails = userDetailsService.loadUserByUsername(reqDto.username());
                var token = jwtService.generateToken(userDetails);
                return new JwtResDto(token);
            }
        } catch (IOException e) {
            log.error("Error:", e);
        }

        throw new LibraryException("invalid_user");
    }
}
