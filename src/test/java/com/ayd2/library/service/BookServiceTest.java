package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServiceTest {
    public static final String ISBN_CORRECT = "123-ABC";
    public static final String ANOTHER_ISBN_CORRECT = "123-BCD";
    public static final String ISBN_INCORRECT = "123-123-123-ABCD";
    public static final String FILTER_WORD = "testWord";

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIsbnCorrect() throws LibraryException {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);

        when(bookRepository.findByIsbn(ISBN_CORRECT)).thenReturn(Optional.of(test));

        Optional<Book> user = bookService.findByISBN(ISBN_CORRECT);

        assertNotNull(user);
    }

    @Test
    void testFindByIsbnIncorrect() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.findByISBN(ISBN_INCORRECT);
        });

        assertEquals("bad_isbn_form",libraryException.getMessage());
    }

    @Test
    void testIsAIsbnValid(){
        boolean isAIsbnValid = bookService.isAIsbnValid(ISBN_CORRECT);
        assertTrue(isAIsbnValid);
        boolean isAIsbnInvalid = bookService.isAIsbnValid(ISBN_INCORRECT);
        assertFalse(isAIsbnInvalid);
    }

    @Test
    void testFindAll() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        List<Book> bookList = bookService.findAllBooks();

        assertNotNull(bookList);
    }

    @Test
    void testFilterBooks() {
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCaseOrIsbnContainingIgnoreCase(anyString(),anyString(),anyString(),anyString())).
                thenReturn(new ArrayList<>());

        List<Book> bookList = bookService.filterBooks(FILTER_WORD);

        assertNotNull(bookList);
    }

    @Test
    void testCreateCorrect() throws LibraryException {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(1);

        when(bookRepository.findByIsbn(ISBN_CORRECT)).thenReturn(Optional.empty());
        when(bookRepository.save(test)).thenReturn(test);

        Book bookCreated = bookService.createBook(test);

        assertNotNull(bookCreated);

        verify(bookRepository).findByIsbn(ISBN_CORRECT);
        verify(bookRepository).save(bookCreated);
    }

    @Test
    void testCreateInvalidIsbnException() {
        Book test = new Book();
        test.setIsbn(ISBN_INCORRECT);
        test.setCopies(1);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.createBook(test);
        });

        assertEquals("bad_isbn_form", libraryException.getMessage());
    }

    @Test
    void testCreateInvalidCopiesException() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(-10);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.createBook(test);
        });

        assertEquals("bad_number_copies", libraryException.getMessage());
    }

    @Test
    void testCreateExistsException() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(1);

        when(bookRepository.findByIsbn(ISBN_CORRECT)).thenReturn(Optional.of(test));

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.createBook(test);
        });

        assertEquals("book_exists", libraryException.getMessage());

        verify(bookRepository).findByIsbn(ISBN_CORRECT);
    }

    @Test
    void testUpdateCorrect() throws LibraryException {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(1);

        when(bookRepository.findByIsbn(ISBN_CORRECT)).thenReturn(Optional.of(test));
        when(bookRepository.save(test)).thenReturn(test);

        Book bookUpdated = bookService.updateBook(ISBN_CORRECT, test);

        assertNotNull(bookUpdated);

        verify(bookRepository).findByIsbn(ISBN_CORRECT);
        verify(bookRepository).save(bookUpdated);
    }

    @Test
    void testUpdateDifferentIsbnException() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(1);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.updateBook(ANOTHER_ISBN_CORRECT, test);
        });

        assertEquals("not_the_same_isbn", libraryException.getMessage());
    }

    @Test
    void testUpdateInvalidCopiesException() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(-1);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.updateBook(ISBN_CORRECT, test);
        });

        assertEquals("bad_number_copies", libraryException.getMessage());
    }

    @Test
    void testUpdateNotExistsException() {
        Book test = new Book();
        test.setIsbn(ISBN_CORRECT);
        test.setCopies(1);

        when(bookRepository.findByIsbn(ISBN_CORRECT)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookService.updateBook(ISBN_CORRECT, test);
        });

        assertEquals("book_doesnt_exist", libraryException.getMessage());

        verify(bookRepository).findByIsbn(ISBN_CORRECT);
    }

    @Test
    void testSaveChangesBook(){
        Book toSave = new Book();
        toSave.setIsbn(ISBN_CORRECT);
        when(bookRepository.save(toSave)).thenReturn(toSave);

        Book tested = bookService.saveChangesBook(toSave);

        assertEquals(toSave, tested);
    }

    @Test
    void testFindByAvailableCopies(){
        when(bookRepository.findByAvailableCopies(0)).thenReturn(new ArrayList<>());

        List<Book> tested = bookService.findByAvailableCopies();

        assertEquals(0, tested.size());
    }

    @Test
    void testBooksNeverLended(){
        when(bookRepository.booksNeverLended()).thenReturn(new ArrayList<>());

        List<Book> tested = bookService.booksNeverLended();

        assertEquals(0, tested.size());
    }
}
