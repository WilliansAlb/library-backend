package com.ayd2.library.service;

import com.ayd2.library.dto.BookingRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Booking;
import com.ayd2.library.model.Student;
import com.ayd2.library.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookService bookService;
    @Mock
    private StudentService studentService;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void testBookingsByAllStudentAndIntervalDate(){
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 8, 10);
        Student student = new Student("-1", null, null);
        when(bookingRepository.findAllByReleaseDateBetweenAndLimitDateAfterOrReleaseDateNull(startDate, endDate, todayDate))
                .thenReturn(new ArrayList<>());

        List<Booking> tested = bookingService.findBookingsByStudentAndIntervalDate(student, startDate, endDate, todayDate);

        assertEquals(0, tested.size());
    }

    @Test
    void testBookingsByStudentAndIntervalDate(){
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 8, 10);
        Student student = new Student("201830221", null, null);
        when(bookingRepository.findAllByStudentAndReleaseDateBetweenAndLimitDateAfterOrStudentAndReleaseDateNull(
                student, startDate, endDate, todayDate, student))
                .thenReturn(new ArrayList<>());

        List<Booking> tested = bookingService.findBookingsByStudentAndIntervalDate(student, startDate, endDate, todayDate);

        assertEquals(0, tested.size());
    }

    @Test
    void testCreateBooking() throws LibraryException {
        Student student = new Student("201830221", null, null);
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        BookingRequest request = new BookingRequest();
        request.setBook(book);
        request.setStudent(student);

        Booking booking = new Booking();
        booking.setBook(book);
        booking.setStudent(student);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.of(book));
        when(studentService.findByLicense("201830221")).thenReturn(Optional.of(student));
        when(bookingRepository.findByBookAndStudentAndReleaseDateNull(book, student)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking tested = bookingService.createBooking(request);

        assertEquals(student, tested.getStudent());
        assertEquals(book, tested.getBook());

        verify(bookService).findByISBN("123-ASB");
        verify(studentService).findByLicense("201830221");
        verify(bookingRepository).findByBookAndStudentAndReleaseDateNull(book, student);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBookingBookDoesntExists() throws LibraryException {
        Student student = new Student("201830221", null, null);
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        BookingRequest request = new BookingRequest();
        request.setBook(book);
        request.setStudent(student);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.empty());

        LibraryException tested = assertThrows(LibraryException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("The book doesnt exists", tested.getMessage());
        verify(bookService).findByISBN("123-ASB");
    }

    @Test
    void testCreateBookingAvailableCopiesException() throws LibraryException {
        Student student = new Student("201830221", null, null);
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(5);

        BookingRequest request = new BookingRequest();
        request.setBook(book);
        request.setStudent(student);

        Booking booking = new Booking();
        booking.setBook(book);
        booking.setStudent(student);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.of(book));

        LibraryException tested = assertThrows(LibraryException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("The book already has available copies", tested.getMessage());
        verify(bookService).findByISBN("123-ASB");
    }

    @Test
    void testCreateBookingStudentNotExists() throws LibraryException {
        Student student = new Student("201830221", null, null);
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        BookingRequest request = new BookingRequest();
        request.setBook(book);
        request.setStudent(student);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.of(book));
        when(studentService.findByLicense("201830221")).thenReturn(Optional.empty());

        LibraryException tested = assertThrows(LibraryException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("The student doesnt exists", tested.getMessage());
        verify(bookService).findByISBN("123-ASB");
        verify(studentService).findByLicense("201830221");
    }

    @Test
    void testCreateBookingAlreadyInListException() throws LibraryException {
        Student student = new Student("201830221", null, null);
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        BookingRequest request = new BookingRequest();
        request.setBook(book);
        request.setStudent(student);

        Booking booking = new Booking();
        booking.setBook(book);
        booking.setStudent(student);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.of(book));
        when(studentService.findByLicense("201830221")).thenReturn(Optional.of(student));
        when(bookingRepository.findByBookAndStudentAndReleaseDateNull(book, student)).thenReturn(Optional.of(booking));

        LibraryException tested = assertThrows(LibraryException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("The book is already in the booking list", tested.getMessage());

        verify(bookService).findByISBN("123-ASB");
        verify(studentService).findByLicense("201830221");
        verify(bookingRepository).findByBookAndStudentAndReleaseDateNull(book, student);
    }

    @Test
    void testUpdateBooking() throws LibraryException {
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        LocalDate releaseDate = LocalDate.of(2024, 8, 10);
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBook(book);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.of(book));
        when(bookingRepository.findAllByBookAndLimitDateNull(book)).thenReturn(bookingList);

        assertTrue(bookingService.updateBooking(book, releaseDate));

        verify(bookService).findByISBN("123-ASB");
        verify(bookingRepository).findAllByBookAndLimitDateNull(book);
    }

    @Test
    void testUpdateBookingBookNotExists() throws LibraryException {
        Book book = new Book();
        book.setIsbn("123-ASB");
        book.setAvailableCopies(0);

        LocalDate releaseDate = LocalDate.of(2024, 8, 10);

        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            bookingService.updateBooking(book, releaseDate);
        });

        assertEquals("The book doesnt exists", libraryException.getMessage());

        verify(bookService).findByISBN("123-ASB");
    }
}
