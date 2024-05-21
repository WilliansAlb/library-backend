package com.ayd2.library.controller;

import com.ayd2.library.dto.UserStudentRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserLibrary>> findAll() {
        var users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLibrary> findById(@PathVariable("id") Long userId) {
        return userService.findById(userId)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserLibrary> create(@RequestBody UserLibrary user) throws LibraryException {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @PostMapping("/student")
    public ResponseEntity<Student> create(@RequestBody UserStudentRequest user) throws LibraryException {
        return new ResponseEntity<>(userService.createStudent(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserLibrary> update(@PathVariable("id") Long userId, @RequestBody UserLibrary entity) throws LibraryException {
        var updatedUser = userService.update(userId, entity);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/student")
    public ResponseEntity<Student> findStudentByLicense(
            @RequestParam("username") String username
    ) throws LibraryException {
        return new ResponseEntity<>(userService.findStudentByUsername(username), HttpStatus.OK);
    }
}
