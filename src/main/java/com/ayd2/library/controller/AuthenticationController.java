package com.ayd2.library.controller;

import com.ayd2.library.dto.AuthRequest;
import com.ayd2.library.dto.JwtRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JwtRequest> createToken(@RequestBody AuthRequest reqDto) throws LibraryException {
        var token = authenticationService.createToken(reqDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
