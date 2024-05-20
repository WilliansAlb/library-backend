package com.ayd2.library.controller;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Student;
import com.ayd2.library.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllCareers(){
        List<Student> studentList = studentService.findAllStudents();
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @GetMapping("/hasPendingInvitation/{license}")
    public ResponseEntity<Boolean> hasPendingInvitation(@PathVariable("license") String license) throws LibraryException {
        Optional<Student> student = studentService.findByLicense(license);
        if (student.isEmpty()) throw new LibraryException("student_doesnt_exists");
        return new ResponseEntity<>(student.get().getUserLibrary()==null, HttpStatus.OK);
    }
}
