package com.ayd2.library.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Clear the security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_AuthenticationSuccessful() throws ServletException, IOException {
        String jwtToken = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.getUsername(jwtToken)).thenReturn("testUser");
        when(jwtService.isValid(jwtToken)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(
                User.withUsername("testUser")
                        .password("password")
                        .roles("librarian")
                        .build()
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidToken_RequestNull() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidToken_NotBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("notbearer");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidToken_UsernameNull() throws ServletException, IOException {
        String jwtToken = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.getUsername(jwtToken)).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidToken_NotValid() throws ServletException, IOException {
        String jwtToken = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.getUsername(jwtToken)).thenReturn("testUser");
        when(jwtService.isValid(jwtToken)).thenReturn(false);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(
                User.withUsername("testUser")
                        .password("password")
                        .roles("librarian")
                        .build()
        );
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
