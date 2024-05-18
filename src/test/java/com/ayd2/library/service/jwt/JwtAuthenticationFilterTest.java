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
        // Mock HttpServletRequest
        String jwtToken = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.getUsername(jwtToken)).thenReturn("testUser");
        when(jwtService.isValid(jwtToken)).thenReturn(true);

        // Mock UserDetailsService behavior
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(
                User.withUsername("testUser")
                        .password("password")
                        .roles("librarian")
                        .build()
        );

        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Perform filter operation
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify authentication setup
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        // Verify filter chain invocation
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
