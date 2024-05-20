package com.ayd2.library.controller;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> bookList = bookService.findAllBooks();
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    @GetMapping("/search/{search}")
    public ResponseEntity<List<Book>> getBooksBySearch(@PathVariable("search") String search) {
        List<Book> bookList = bookService.filterBooks(search);
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book toCreate) throws LibraryException {
        return new ResponseEntity<>(bookService.createBook(toCreate),HttpStatus.CREATED);
    }

    @PutMapping("/update/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable("isbn") String isbn, @RequestBody Book toCreate) throws LibraryException {
        return new ResponseEntity<>(bookService.updateBook(isbn,toCreate),HttpStatus.OK);
    }
}
