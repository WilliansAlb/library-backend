package com.ayd2.library.repository;

import com.ayd2.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String title,
            String author,
            String publisher,
            String isbn);

    List<Book> findByAvailableCopies(int availableCopies);

    @Query("SELECT b FROM Book b LEFT JOIN Loan l ON b.isbn = l.book.isbn WHERE l.book IS NULL")
    List<Book> booksNeverLended();
}
