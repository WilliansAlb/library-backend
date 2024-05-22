package com.ayd2.library.repository;

import com.ayd2.library.model.Book;
import com.ayd2.library.model.Booking;
import com.ayd2.library.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByReleaseDateBetweenAndLimitDateAfterOrReleaseDateNull(LocalDate startDate, LocalDate endDate, LocalDate todayDate);

    List<Booking> findAllByStudentAndReleaseDateBetweenAndLimitDateAfterOrStudentAndReleaseDateNull(
            Student student, LocalDate startDate, LocalDate endDate, LocalDate todayDate, Student student2);

    List<Booking> findAllByBookAndLimitDateNull(Book book);

    Optional<Booking> findByBookAndStudentAndReleaseDateNull(Book book, Student student);
}
