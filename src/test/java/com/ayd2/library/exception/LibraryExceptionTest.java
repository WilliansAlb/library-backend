package com.ayd2.library.exception;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LibraryExceptionTest {

    @Test
    public void testDefaultConstructor() {
        LibraryException exception = new LibraryException();
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertNull(exception.getMessage());
    }

    @Test
    public void testMessageConstructor() {
        String message = "Test error message";
        LibraryException exception = new LibraryException(message);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testStatusMethod() {
        String message = "Test error message";
        LibraryException exception = new LibraryException(message);
        exception.status(HttpStatus.NOT_FOUND);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }
}
