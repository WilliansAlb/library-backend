package com.ayd2.library.service;

import com.ayd2.library.dto.*;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.*;
import com.ayd2.library.repository.LoanRepository;
import com.ayd2.library.util.LibraryConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final StudentService studentService;
    private final BookingService bookingService;
    private final BookService bookService;


    public List<Loan> findLoansByStudentAndIntervalDate(Student student, LocalDate startDate, LocalDate endDate) {
        if (student.getLicense().equalsIgnoreCase("-1")) {
            return loanRepository.findAllByLoanDateBetween(startDate, endDate);
        }
        return loanRepository.findAllByStudentAndLoanDateBetween(student, startDate, endDate);
    }

    public List<Loan> findActualLoansByStudent(Student student) {
        return loanRepository.findAllByStudentAndReturnDateNull(student);
    }

    public List<LoanStudentResponse> createLoans(LoanStudentRequest request) throws LibraryException {
        if (studentService.findByLicense(request.getStudent().getLicense()).isEmpty())
            throw new LibraryException("The student doesnt exists");
        if (loanRepository.listOverdueLoans(request.getStudent().getLicense(), request.getTodayDate()).size() > 0) {
            throw new LibraryException("The student is already sanctioned");
        }
        List<LoanStudentResponse> responseList = new ArrayList<>();
        List<Loan> loanList = findActualLoansByStudent(request.getStudent());
        Map<String, Boolean> bookLend = new HashMap<>();
        for (Loan lend : loanList) {
            bookLend.put(lend.getBook().getIsbn(), true);
        }
        int totalLoans = loanList.size();
        if (totalLoans + request.getBookList().size() > 3) {
            throw new LibraryException("limit_3_books_exceeded");
        }
        for (Book book : request.getBookList()) {
            Optional<Book> toAdd = bookService.findByISBN(book.getIsbn());
            if (toAdd.isPresent() && toAdd.get().getAvailableCopies() > 0 && !bookLend.containsKey(toAdd.get().getIsbn())) {
                Loan newLoan = new Loan();
                newLoan.setStudent(request.getStudent());
                newLoan.setLoanDate(request.getLoanDate());
                if (request.getExpectedDate() == null) {
                    newLoan.setExpectedDate(request.getLoanDate().plusDays(3));
                } else {
                    newLoan.setExpectedDate(request.getExpectedDate());
                }
                newLoan.setBook(toAdd.get());
                toAdd.get().setAvailableCopies(toAdd.get().getAvailableCopies() - 1);
                bookService.saveChangesBook(toAdd.get());
                LoanStudentResponse response = new LoanStudentResponse();
                response.setToLoan(toAdd.get());
                response.setLended(true);
                responseList.add(response);
                loanRepository.save(newLoan);
            } else {
                LoanStudentResponse response = new LoanStudentResponse();
                response.setToLoan(book);
                response.setLended(false);
                if (toAdd.isEmpty()) {
                    response.setReasonForNotLend("The book doesnt exists");
                } else {
                    if (toAdd.get().getAvailableCopies() <= 0) {
                        response.setReasonForNotLend("The book doesnt have copies");
                    } else {
                        response.setReasonForNotLend("The book is already lended by the student");
                    }
                }
                responseList.add(response);
            }
        }
        return responseList;
    }

    public BalanceLoanResponse getBalanceLoanResponse(Long loanId, LocalDate todayDate) throws LibraryException {
        Optional<Loan> exist = loanRepository.findById(loanId);
        if (exist.isEmpty()) throw new LibraryException("The loan doesnt exist");
        BalanceLoanResponse response = new BalanceLoanResponse();
        response.setLoan(exist.get());
        response.setSanction((DAYS.between(exist.get().getExpectedDate(), todayDate) >= 30) ? LibraryConstant.PAYMENT_SANCTION : 0);
        double totalLate = getPayment(exist.get().getExpectedDate(), todayDate, LibraryConstant.PAYMENT_LATE);
        double totalNormal = 0;
        if (DAYS.between(exist.get().getExpectedDate(), todayDate) > 0) {
            totalNormal = getPayment(exist.get().getLoanDate(), exist.get().getExpectedDate(), LibraryConstant.PAYMENT_NORMAL);
        } else {
            totalNormal = getPayment(exist.get().getLoanDate(), todayDate, LibraryConstant.PAYMENT_NORMAL);
        }
        response.setTotalLate(totalLate > 0 ? totalLate : 0);
        response.setTotalNormal(totalNormal > 0 ? totalNormal : 0);
        return response;
    }

    public Loan payLoan(PayLoanRequest request) throws LibraryException {
        BalanceLoanResponse balance = getBalanceLoanResponse(request.getToPay().getLoanId(), request.getTodayDate());
        Loan saved = balance.getLoan();
        saved.setReturnDate(request.getTodayDate());
        saved.setLoanPayment(BigDecimal.valueOf(balance.getTotalNormal()));
        double total = balance.getTotalLate() + balance.getSanction();
        saved.setLatePayment(BigDecimal.valueOf(total));
        saved.setPenaltyPayment(balance.getSanction() > 0);
        Book returned = balance.getLoan().getBook();
        returned.setAvailableCopies(returned.getAvailableCopies() + 1);
        saved = loanRepository.save(saved);
        returned = bookService.saveChangesBook(returned);
        bookingService.updateBooking(returned, request.getTodayDate());
        return saved;
    }

    public double getPayment(LocalDate startDate, LocalDate endDate, double amount) {
        return DAYS.between(startDate, endDate) * amount;
    }

    public List<Loan> loansForToday(LocalDate todayDate) {
        return loanRepository.findAllByExpectedDateAndReturnDateNull(todayDate);
    }

    public List<Loan> overdueLoans(LocalDate todayDate) {
        return loanRepository.listLateLoans(todayDate);
    }

    public BalanceReportResponse balance(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalNormal = loanRepository.getNormalTotal(startDate, endDate);
        BigDecimal totalLate = loanRepository.getLateTotal(startDate, endDate);
        List<Loan> loanList = loanRepository.findAllByLoanDateBetween(startDate, endDate);
        BalanceReportResponse response = new BalanceReportResponse();
        response.setLoanList(loanList);
        response.setTotalNormal(totalNormal);
        response.setTotalLate(totalLate);
        return response;
    }

    public TopCareerReportResponse topCareer(LocalDate startDate, LocalDate endDate) throws LibraryException {
        List<LoanCountByCareer> countByCareers = loanRepository.getCareerTopLoans(startDate, endDate);
        if (countByCareers.size() == 0) throw new LibraryException("No result for this report");
        Long count = countByCareers.get(0).getLoanCount();
        Career found = countByCareers.get(0).getCareer();
        List<Loan> loanList = loanRepository.findAllByStudentCareerAndLoanDateBetween(found, startDate, endDate);
        TopCareerReportResponse response = new TopCareerReportResponse();
        response.setCareer(found);
        response.setCount(count);
        response.setLoanList(loanList);
        return response;
    }

    public List<Loan> loansByStudent(Student student, LocalDate startDate, LocalDate endDate) {
        return loanRepository.findAllByStudentAndLatePaymentNotNullAndReturnDateBetween(student, startDate, endDate);
    }

    public TopStudentReportResponse topStudent(LocalDate startDate, LocalDate endDate) throws LibraryException {
        List<LoanCountByStudent> loanCountByStudent = loanRepository.topStudent(startDate, endDate);
        if (loanCountByStudent.size() == 0) throw new LibraryException("No result for this report");
        Long count = loanCountByStudent.get(0).getLoanCount();
        Student student = loanCountByStudent.get(0).getStudent();
        List<Loan> loanList = loanRepository.findAllByStudentAndLoanDateBetween(student, startDate, endDate);
        TopStudentReportResponse response = new TopStudentReportResponse();
        response.setCount(count);
        response.setStudent(student);
        response.setLoanList(loanList);
        return response;
    }

    public List<Loan> loansWithOverdue(LocalDate todayDate) {
        return loanRepository.loansWithOverdue(todayDate);
    }
}
