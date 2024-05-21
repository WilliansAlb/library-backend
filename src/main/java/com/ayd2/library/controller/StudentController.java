package com.ayd2.library.controller;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Student;
import com.ayd2.library.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllCareers(){
        return new ResponseEntity<>(studentService.findAllStudents(), HttpStatus.OK);
    }

    @GetMapping("/hasPendingInvitation/{license}")
    public ResponseEntity<Boolean> hasPendingInvitation(@PathVariable("license") String license) throws LibraryException {
        return new ResponseEntity<>(studentService.hasPendingInvitation(license), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student toCreate) throws LibraryException {
        return new ResponseEntity<>(studentService.createStudent(toCreate), HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchByWordAndFilterByCareer(
            @RequestParam("search") String search,
            @RequestParam("careerId") Long careerId
    ){
        return new ResponseEntity<>(studentService.filterStudent(search, careerId), HttpStatus.OK);
    }
}
