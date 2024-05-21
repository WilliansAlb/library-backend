package com.ayd2.library.repository;

import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByLicenseAndNameAndBirthdayAndUserLibraryIsNull(String license, String name, LocalDate birthday);

    List<Student> findByLicenseOrNameContainingIgnoreCase(String license, String name);

    Optional<Student> findByUserLibrary(UserLibrary userLibrary);
}
