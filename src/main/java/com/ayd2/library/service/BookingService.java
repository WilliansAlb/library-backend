package com.ayd2.library.service;

import com.ayd2.library.dto.BookingRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Booking;
import com.ayd2.library.model.Student;
import com.ayd2.library.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookService bookService;
    private final StudentService studentService;
    private final BookingRepository bookingRepository;

    public List<Booking> findBookingsByStudentAndIntervalDate(Student student, LocalDate startDate, LocalDate endDate, LocalDate todayDate) {
        if (student.getLicense().equalsIgnoreCase("-1")) {
            return bookingRepository.findAllByReleaseDateBetweenAndLimitDateAfterOrReleaseDateNull(startDate, endDate, todayDate);
        }
        return bookingRepository.findAllByStudentAndReleaseDateBetweenAndLimitDateAfterOrStudentAndReleaseDateNull(student, startDate, endDate, todayDate, student);
    }

    public Booking createBooking(BookingRequest request) throws LibraryException {
        Optional<Book> book = bookService.findByISBN(request.getBook().getIsbn());
        if (book.isEmpty()) throw new LibraryException("The book doesnt exists");
        if (book.get().getAvailableCopies() > 0) throw new LibraryException("The book already has available copies");
        Optional<Student> student = studentService.findByLicense(request.getStudent().getLicense());
        if (student.isEmpty()) throw new LibraryException("The student doesnt exists");
        Optional<Booking> booking = bookingRepository.findByBookAndStudentAndReleaseDateNull(book.get(), student.get());
        if (booking.isPresent()) throw new LibraryException("The book is already in the booking list");
        Booking toCreate = new Booking();
        toCreate.setStudent(student.get());
        toCreate.setBook(book.get());
        return bookingRepository.save(toCreate);
    }

    public boolean updateBooking(Book book, LocalDate releaseDate) throws LibraryException {
        Optional<Book> found = bookService.findByISBN(book.getIsbn());
        if (found.isEmpty()) throw new LibraryException("The book doesnt exists");
        List<Booking> bookingList = bookingRepository.findAllByBookAndLimitDateNull(found.get());
        LocalDate limitDate = releaseDate.plusDays(1);
        for(Booking booking: bookingList){
            booking.setReleaseDate(releaseDate);
            booking.setLimitDate(limitDate);
            bookingRepository.save(booking);
        }
        return true;
    }
}
