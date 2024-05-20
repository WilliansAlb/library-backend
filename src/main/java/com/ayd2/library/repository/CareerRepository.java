package com.ayd2.library.repository;

import com.ayd2.library.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRepository extends JpaRepository<Career, Long> {

    List<Career> findByNameContainingIgnoreCase(String name);

    Optional<Career> findByName(String name);
}
