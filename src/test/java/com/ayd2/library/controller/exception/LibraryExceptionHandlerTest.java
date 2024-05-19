package com.ayd2.library.controller.exception;

import com.ayd2.library.exception.LibraryException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LibraryExceptionHandlerTest {
    LibraryExceptionHandler handler = new LibraryExceptionHandler();

    @Test
    public void testHandleLibraryException() {
        LibraryException libraryException = (new LibraryException("Library error")).status(HttpStatus.BAD_REQUEST);

        ResponseEntity<String> responseEntity = handler.handleLibraryException(libraryException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Library error", responseEntity.getBody());
    }

    @Test
    public void testHandleAuthenticationException() {
        AuthenticationException authException = new AuthenticationException("Authentication failed") {};

        ResponseEntity<String> responseEntity = handler.handleAuthenticationException(authException);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("bad_credentials", responseEntity.getBody());
    }
}
