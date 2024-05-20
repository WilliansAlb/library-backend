package com.ayd2.library.repository;

import com.ayd2.library.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByLicenseAndNameAndBirthdayAndUserLibraryIsNull(String license, String name, LocalDate birthday);
}
