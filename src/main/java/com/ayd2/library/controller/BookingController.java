package com.ayd2.library.controller;

import com.ayd2.library.dto.BookingRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Booking;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import com.ayd2.library.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> findBookingsByStudentAndIntervalDate(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("todayDate") LocalDate todayDate,
            @RequestParam("license") String license
    ) {
        Student student = new Student();
        student.setLicense(license);
        return new ResponseEntity<>(bookingService.findBookingsByStudentAndIntervalDate(student, startDate, endDate, todayDate), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) throws LibraryException {
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }
}
