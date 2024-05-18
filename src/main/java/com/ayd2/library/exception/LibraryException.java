package com.ayd2.library.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LibraryException extends Exception {

    private HttpStatus status;

    public LibraryException() {
        this.status = HttpStatus.BAD_REQUEST;
    }

    public LibraryException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public LibraryException status(HttpStatus status) {
        this.status = status;
        return this;
    }
}
