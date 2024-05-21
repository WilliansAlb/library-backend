package com.ayd2.library.controller;

import com.ayd2.library.dto.BalanceLoanRequest;
import com.ayd2.library.dto.BalanceLoanResponse;
import com.ayd2.library.dto.LoanStudentRequest;
import com.ayd2.library.dto.LoanStudentResponse;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import com.ayd2.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<List<LoanStudentResponse>> createLoans(@RequestBody LoanStudentRequest request) throws LibraryException {
        return new ResponseEntity<>(loanService.createLoans(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("license") String license
    ) {
        Student student = new Student();
        student.setLicense(license);
        return new ResponseEntity<>(loanService.findLoansByStudentAndIntervalDate(student, startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceLoanResponse> getBalanceLoan(
            @RequestParam("loanId") Long loanId,
            @RequestParam("todayDate") LocalDate todayDate
    ) throws LibraryException {
        return new ResponseEntity<>(loanService.getBalanceLoanResponse(loanId, todayDate), HttpStatus.OK);
    }
}
