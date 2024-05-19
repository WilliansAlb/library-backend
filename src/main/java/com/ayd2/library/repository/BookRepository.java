package com.ayd2.library.repository;

import com.ayd2.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);
}
