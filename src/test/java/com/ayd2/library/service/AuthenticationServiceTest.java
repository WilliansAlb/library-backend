package com.ayd2.library.service;

import com.ayd2.library.dto.AuthRequest;
import com.ayd2.library.dto.JwtRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateToken_Success() throws LibraryException {
        AuthRequest authRequest = new AuthRequest("user", "password");
        UsernamePasswordAuthenticationToken authData =
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authData)).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(authRequest.username())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        JwtRequest jwtRequest = authenticationService.createToken(authRequest);

        assertNotNull(jwtRequest);
        assertEquals("jwt-token", jwtRequest.token());

        verify(authenticationManager, times(1)).authenticate(authData);
        verify(userDetailsService, times(1)).loadUserByUsername(authRequest.username());
        verify(jwtService, times(1)).generateToken(userDetails);
    }

    @Test
    void testCreateToken_InvalidUser() {
        AuthRequest authRequest = new AuthRequest("user", "password");
        UsernamePasswordAuthenticationToken authData =
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(authData)).thenReturn(authentication);

        LibraryException thrown = assertThrows(LibraryException.class, () -> {
            authenticationService.createToken(authRequest);
        });

        assertEquals("invalid_user", thrown.getMessage());
    }
}
