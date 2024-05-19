package com.ayd2.library.controller;

import com.ayd2.library.dto.AuthRequest;
import com.ayd2.library.dto.JwtRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateToken_Success() throws LibraryException {
        AuthRequest authRequest = new AuthRequest("will","sdfa");
        JwtRequest jwtRequest = new JwtRequest("saf");
        when(authenticationService.createToken(authRequest)).thenReturn(jwtRequest);

        ResponseEntity<JwtRequest> response = authenticationController.createToken(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtRequest, response.getBody());
        verify(authenticationService, times(1)).createToken(authRequest);
    }

    @Test
    void testCreateToken_Failure() throws LibraryException {
        AuthRequest authRequest = new AuthRequest("will","testPass");
        when(authenticationService.createToken(authRequest)).thenThrow(new LibraryException("Invalid credentials"));

        try {
            authenticationController.createToken(authRequest);
        } catch (LibraryException e) {
            assertEquals("Invalid credentials", e.getMessage());
        }

        verify(authenticationService, times(1)).createToken(authRequest);
    }
}
