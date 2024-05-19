package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    public Optional<Book> findByISBN(String isbn) throws LibraryException {
        if (isAIsbnValid(isbn)){
            isbn = isbn.replaceAll("-","");
            return bookRepository.findByIsbn(isbn);
        } else {
            throw new LibraryException("bad_isbn_form");
        }
    }

    public boolean isAIsbnValid(String toValidate){
        String regex = "(\\d{3}-[A-Z]{3})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(toValidate);
        return matcher.matches();
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Book createBook(Book toCreate) throws LibraryException{
        if (!isAIsbnValid(toCreate.getIsbn())){
            throw new LibraryException("bad_isbn_form");
        }
        if (toCreate.getCopies() <= 0){
            throw new LibraryException("bad_number_copies");
        }
        Optional<Book> bookSaved = bookRepository.findByIsbn(toCreate.getIsbn());
        if (bookSaved.isPresent()) {
            throw new LibraryException("book_exists");
        }
        return bookRepository.save(toCreate);
    }

    public Book updateBook(String isbn, Book toUpdate) throws LibraryException {
        if (!isbn.equals(toUpdate.getIsbn())){
            throw new LibraryException("not_the_same_isbn");
        }
        if (toUpdate.getCopies() <= 0){
            throw new LibraryException("bad_number_copies");
        }
        Optional<Book> bookSaved = bookRepository.findByIsbn(isbn);
        if (bookSaved.isEmpty()){
            throw new LibraryException("book_doesnt_exist");
        }
        return bookRepository.save(toUpdate);
    }
}