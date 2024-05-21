package com.ayd2.library.service;

import com.ayd2.library.dto.LoanStudentRequest;
import com.ayd2.library.dto.LoanStudentResponse;
import com.ayd2.library.dto.BalanceLoanResponse;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import com.ayd2.library.repository.LoanRepository;
import com.ayd2.library.util.LibraryConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final StudentService studentService;
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
            System.out.println(toAdd.isPresent() + book.getIsbn());
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
                if (toAdd.isEmpty()) response.setReasonForNotLend("The book doesnt exists");
                if (toAdd.isPresent() && toAdd.get().getAvailableCopies() <= 0)
                    response.setReasonForNotLend("The book doesnt have copies");
                if (toAdd.isPresent() && bookLend.containsKey(toAdd.get().getIsbn()))
                    response.setReasonForNotLend("The book is already lended by the student");
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
        double totalNormal = getPayment(exist.get().getLoanDate(), exist.get().getExpectedDate(), LibraryConstant.PAYMENT_NORMAL);
        response.setTotalLate(totalLate > 0 ? totalLate : 0);
        response.setTotalNormal(totalNormal > 0 ? totalNormal : 0);
        return response;
    }

    public double getPayment(LocalDate startDate, LocalDate endDate, double amount) {
        return DAYS.between(startDate, endDate) * amount;
    }
}
