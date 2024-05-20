package com.ayd2.library.service;

import com.ayd2.library.model.Student;
import com.ayd2.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final StudentRepository studentRepository;

    public List<Student> findAllStudents(){
        return studentRepository.findAll();
    }

    public Optional<Student> findByLicenseAndNameAndBirthday(String license, String name, LocalDate birthday){
        return studentRepository.findByLicenseAndNameAndBirthdayAndUserLibraryIsNull(license,name,birthday);
    }

    public Optional<Student> findByLicense(String license) {
        return studentRepository.findById(license);
    }

    public Student saveStudent(Student student){
        return studentRepository.save(student);
    }
}
