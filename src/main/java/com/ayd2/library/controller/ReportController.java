package com.ayd2.library.controller;

import com.ayd2.library.dto.BalanceReportResponse;
import com.ayd2.library.dto.TopCareerReportResponse;
import com.ayd2.library.dto.TopStudentReportResponse;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import com.ayd2.library.service.BookService;
import com.ayd2.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("reports")
@RequiredArgsConstructor
public class ReportController {
    private final LoanService loanService;
    private final BookService bookService;

    @GetMapping("/loansForToday")
    public ResponseEntity<List<Loan>> loansForToday(
            @RequestParam("todayDate") LocalDate todayDate
    ) {
        return new ResponseEntity<>(loanService.loansForToday(todayDate), HttpStatus.OK);
    }

    @GetMapping("/overdueLoans")
    public ResponseEntity<List<Loan>> overdueLoans(
            @RequestParam("todayDate") LocalDate todayDate
    ) {
        return new ResponseEntity<>(loanService.overdueLoans(todayDate), HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceReportResponse> balance(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        return new ResponseEntity<>(loanService.balance(startDate, endDate), HttpStatus.OK);
    }
    @GetMapping("/topCareer")
    public ResponseEntity<TopCareerReportResponse> topCareer(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) throws LibraryException {
        return new ResponseEntity<>(loanService.topCareer(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/topStudent")
    public ResponseEntity<TopStudentReportResponse> topStudent(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) throws LibraryException {
        return new ResponseEntity<>(loanService.topStudent(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/notAvailableCopies")
    public ResponseEntity<List<Book>> notAvailableCopies(){
        return new ResponseEntity<>(bookService.findByAvailableCopies(), HttpStatus.OK);
    }

    @GetMapping("/currentlyLendedByStudent")
    public ResponseEntity<List<Loan>> currentlyLendedByStudent(
            @RequestParam("license") String license
    ){
        Student request = new Student();
        request.setLicense(license);
        return new ResponseEntity<>(loanService.findActualLoansByStudent(request), HttpStatus.OK);
    }

    @GetMapping("/loansByStudent")
    public ResponseEntity<List<Loan>> loansByStudent(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("license") String license
    ) {
        Student request = new Student();
        request.setLicense(license);
        return new ResponseEntity<>(loanService.loansByStudent(request, startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/booksNeverLended")
    public ResponseEntity<List<Book>> booksNeverLended(){
        return new ResponseEntity<>(bookService.booksNeverLended(), HttpStatus.OK);
    }

    @GetMapping("/loansWithOverdue")
    public ResponseEntity<List<Loan>> loansWithOverdue(
            @RequestParam("todayDate") LocalDate todayDate
    ){
        return new ResponseEntity<>(loanService.loansWithOverdue(todayDate), HttpStatus.OK);
    }
}
