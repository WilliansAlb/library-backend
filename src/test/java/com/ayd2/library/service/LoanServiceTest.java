package com.ayd2.library.service;

import com.ayd2.library.dto.*;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import com.ayd2.library.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {
    public static final String LICENSE_NUMBER = "201830221";
    public static final long LOAN_ID = 1L;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private StudentService studentService;
    @Mock
    private BookingService bookingService;
    @Mock
    private BookService bookService;
    @InjectMocks
    private LoanService loanService;

    @Test
    void testLoansByStudentAndIntervalDate() {
        Student student = new Student(LICENSE_NUMBER, null, null);
        when(loanRepository.findAllByStudentAndLoanDateBetween(any(Student.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.findLoansByStudentAndIntervalDate(student, LocalDate.now(), LocalDate.now());

        assertEquals(0, tested.size());
        verify(loanRepository).findAllByStudentAndLoanDateBetween(any(Student.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testLoansByAllStudentsAndIntervalDate() {
        Student student = new Student("-1", null, null);
        when(loanRepository.findAllByLoanDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.findLoansByStudentAndIntervalDate(student, LocalDate.now(), LocalDate.now());

        assertEquals(0, tested.size());
        verify(loanRepository).findAllByLoanDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testActualLoansByStudent() {
        Student student = new Student("-1", null, null);
        when(loanRepository.findAllByStudentAndReturnDateNull(student)).thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.findActualLoansByStudent(student);

        assertEquals(0, tested.size());
    }


    private LoanStudentRequest createLoanStudentRequest(Student student, List<Book> bookList, LocalDate loanDate, LocalDate todayDate) {
        LoanStudentRequest request = new LoanStudentRequest();
        request.setStudent(student);
        request.setBookList(bookList);
        request.setLoanDate(loanDate);
        request.setTodayDate(todayDate);
        return request;
    }

    @Test
    public void testCreateLoansStudentNotExist() {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        List<Book> bookList = Collections.emptyList();
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.empty());

        // Act & Assert
        LibraryException exception = assertThrows(LibraryException.class, () -> {
            loanService.createLoans(request);
        });

        assertEquals("The student doesnt exists", exception.getMessage());
    }

    @Test
    public void testCreateLoansStudentSanctioned() {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        List<Book> bookList = Collections.emptyList();
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.singletonList(new Loan()));

        // Act & Assert
        LibraryException exception = assertThrows(LibraryException.class, () -> {
            loanService.createLoans(request);
        });

        assertEquals("The student is already sanctioned", exception.getMessage());
    }

    @Test
    public void testCreateLoansLimitExceeded() {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        List<Book> bookList = Arrays.asList(new Book(), new Book(), new Book(), new Book());
        for (int i = 0; i < 4; i++) {
            bookList.get(i).setIsbn(i + "23-ASB");
        }
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        Loan forTest = new Loan();
        Book forTestBook = new Book();
        forTestBook.setIsbn("123-ABC");
        forTest.setBook(forTestBook);
        when(loanService.findActualLoansByStudent(student)).thenReturn(Collections.singletonList(forTest));

        // Act & Assert
        LibraryException exception = assertThrows(LibraryException.class, () -> {
            loanService.createLoans(request);
        });

        assertEquals("limit_3_books_exceeded", exception.getMessage());
    }

    @Test
    public void testCreateLoansSuccess() throws LibraryException {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        Book book1 = new Book();
        book1.setIsbn("123-ABC");
        book1.setAvailableCopies(1);
        List<Book> bookList = Collections.singletonList(book1);
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        when(loanService.findActualLoansByStudent(student)).thenReturn(Collections.emptyList());
        when(bookService.findByISBN(book1.getIsbn())).thenReturn(Optional.of(book1));

        // Act
        List<LoanStudentResponse> responses = loanService.createLoans(request);

        // Assert
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isLended());
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(bookService, times(1)).saveChangesBook(book1);
    }

    @Test
    public void testCreateLoansSuccessBookDoesntExist() throws LibraryException {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        Book book1 = new Book();
        book1.setIsbn("123-ABC");
        book1.setAvailableCopies(1);
        List<Book> bookList = new ArrayList<>();
        Book temp = new Book();
        temp.setIsbn("123-ASB");
        bookList.add(temp);
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        when(loanService.findActualLoansByStudent(student)).thenReturn(Collections.emptyList());
        when(bookService.findByISBN("123-ASB")).thenReturn(Optional.empty());

        // Act
        List<LoanStudentResponse> loans = loanService.createLoans(request);

        // Assert
        assertEquals("The book doesnt exists", loans.get(0).getReasonForNotLend());
    }

    @Test
    public void testCreateLoansSuccessBookAlreadyLended() throws LibraryException {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        Book book1 = new Book();
        book1.setIsbn("123-ABC");
        book1.setAvailableCopies(4);
        List<Book> bookList = new ArrayList<>();
        bookList.add(book1);
        List<Loan> loanList = new ArrayList<>();
        Loan temp = new Loan();
        temp.setBook(book1);
        loanList.add(temp);
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        when(loanService.findActualLoansByStudent(student)).thenReturn(loanList);
        when(bookService.findByISBN("123-ABC")).thenReturn(Optional.of(book1));

        // Act
        List<LoanStudentResponse> loans = loanService.createLoans(request);

        // Assert
        assertEquals("The book is already lended by the student", loans.get(0).getReasonForNotLend());
    }

    @Test
    public void testCreateLoansSuccessExpectedDateNull() throws LibraryException {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        Book book1 = new Book();
        book1.setIsbn("123-ABC");
        book1.setAvailableCopies(1);
        List<Book> bookList = Collections.singletonList(book1);
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());
        request.setExpectedDate(LocalDate.now());
        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        when(loanService.findActualLoansByStudent(student)).thenReturn(Collections.emptyList());
        when(bookService.findByISBN(book1.getIsbn())).thenReturn(Optional.of(book1));

        // Act
        List<LoanStudentResponse> responses = loanService.createLoans(request);

        // Assert
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isLended());
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(bookService, times(1)).saveChangesBook(book1);
    }

    @Test
    public void testCreateLoansBookNotAvailable() throws LibraryException {
        // Arrange
        Student student = new Student();
        student.setLicense("20210001");
        Book book1 = new Book();
        book1.setIsbn("123-ABC");
        book1.setAvailableCopies(0);
        List<Book> bookList = Collections.singletonList(book1);
        LoanStudentRequest request = createLoanStudentRequest(student, bookList, LocalDate.now(), LocalDate.now());

        when(studentService.findByLicense(student.getLicense())).thenReturn(Optional.of(student));
        when(loanRepository.listOverdueLoans(student.getLicense(), request.getTodayDate())).thenReturn(Collections.emptyList());
        when(loanService.findActualLoansByStudent(student)).thenReturn(Collections.emptyList());
        when(bookService.findByISBN(book1.getIsbn())).thenReturn(Optional.of(book1));

        // Act
        List<LoanStudentResponse> responses = loanService.createLoans(request);

        // Assert
        assertEquals(1, responses.size());
        assertFalse(responses.get(0).isLended());
        assertEquals("The book doesnt have copies", responses.get(0).getReasonForNotLend());
        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookService, never()).saveChangesBook(book1);
    }

    @Test
    void testGetBalanceLoan() throws LibraryException {
        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 6, 10);
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        BalanceLoanResponse response = loanService.getBalanceLoanResponse(LOAN_ID, todayDate);

        assertEquals(15, response.getTotalNormal());
        assertEquals(90, response.getTotalLate());
        assertEquals(0, response.getSanction());
    }

    @Test
    void testGetBalanceLoanOver30Days() throws LibraryException {
        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 8, 3);
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        BalanceLoanResponse response = loanService.getBalanceLoanResponse(LOAN_ID, todayDate);

        assertEquals(15, response.getTotalNormal());
        assertEquals(900, response.getTotalLate());
        assertEquals(150, response.getSanction());
    }

    @Test
    void testGetBalanceLoanNotExists() {
        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 8, 3);
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            loanService.getBalanceLoanResponse(LOAN_ID, todayDate);
        });

        assertEquals("The loan doesnt exist", libraryException.getMessage());
    }

    @Test
    void testGetBalanceLoanLateLessThanZero() throws LibraryException {
        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = loanDate;
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        BalanceLoanResponse response = loanService.getBalanceLoanResponse(LOAN_ID, todayDate);

        assertEquals(0, response.getTotalNormal());
        assertEquals(0, response.getTotalLate());
        assertEquals(0, response.getSanction());
    }

    @Test
    void payLoan() throws LibraryException {
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 6, 10);

        Book book = new Book();
        book.setIsbn("123-ABC");
        book.setAvailableCopies(2);

        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        loan.setBook(book);

        PayLoanRequest request = new PayLoanRequest();
        request.setToPay(loan);
        request.setTodayDate(todayDate);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(bookService.saveChangesBook(book)).thenReturn(book);
        when(bookingService.updateBooking(book, todayDate)).thenReturn(true);

        Loan tested = loanService.payLoan(request);

        assertEquals(3, tested.getBook().getAvailableCopies());
    }

    @Test
    void payLoanPenaltyPayment() throws LibraryException {
        LocalDate loanDate = LocalDate.of(2024, 6, 1);
        LocalDate expectedDate = LocalDate.of(2024, 6, 4);
        LocalDate todayDate = LocalDate.of(2024, 8, 10);

        Book book = new Book();
        book.setIsbn("123-ABC");
        book.setAvailableCopies(2);

        Loan loan = new Loan();
        loan.setLoanId(LOAN_ID);
        loan.setLoanDate(loanDate);
        loan.setExpectedDate(expectedDate);
        loan.setBook(book);

        PayLoanRequest request = new PayLoanRequest();
        request.setToPay(loan);
        request.setTodayDate(todayDate);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(bookService.saveChangesBook(book)).thenReturn(book);
        when(bookingService.updateBooking(book, todayDate)).thenReturn(true);

        Loan tested = loanService.payLoan(request);

        assertTrue(tested.isPenaltyPayment());
    }

    @Test
    void testBalance() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);
        when(loanRepository.findAllByLoanDateBetween(startDate, endDate)).thenReturn(new ArrayList<>());
        when(loanRepository.getNormalTotal(startDate, endDate)).thenReturn(BigDecimal.valueOf(15));
        when(loanRepository.getLateTotal(startDate, endDate)).thenReturn(BigDecimal.valueOf(90));

        BalanceReportResponse response = loanService.balance(startDate, endDate);

        assertEquals(BigDecimal.valueOf(15), response.getTotalNormal());
        assertEquals(BigDecimal.valueOf(90), response.getTotalLate());
    }

    @Test
    void testTopCareer() throws LibraryException {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);
        Long count = 5L;
        Career career = new Career(58L, "Sistemas");
        List<LoanCountByCareer> byCareerList = new ArrayList<>();
        LoanCountByCareer countByCareer = new LoanCountByCareer(count, career);
        byCareerList.add(countByCareer);
        when(loanRepository.getCareerTopLoans(startDate,endDate)).thenReturn(byCareerList);
        when(loanRepository.findAllByStudentCareerAndLoanDateBetween(career, startDate, endDate)).thenReturn(new ArrayList<>());

        TopCareerReportResponse tested = loanService.topCareer(startDate, endDate);

        assertEquals(0, tested.getLoanList().size());
        assertEquals(5L, tested.getCount());

        verify(loanRepository).getCareerTopLoans(startDate,endDate);
        verify(loanRepository).findAllByStudentCareerAndLoanDateBetween(career, startDate, endDate);
    }

    @Test
    void testTopCareerNoResult() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);
        when(loanRepository.getCareerTopLoans(startDate,endDate)).thenReturn(new ArrayList<>());

        LibraryException libraryException = assertThrows(LibraryException.class, ()-> {
            loanService.topCareer(startDate, endDate);
        });

        assertEquals("No result for this report", libraryException.getMessage());

        verify(loanRepository).getCareerTopLoans(startDate,endDate);
    }

    @Test
    void testLoansByStudent(){
        Student student = new Student("201830221",new Career(58L, "Sistemas"), "Willians");
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);
        when(loanRepository.findAllByStudentAndLatePaymentNotNullAndReturnDateBetween(student,startDate, endDate)).thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.loansByStudent(student,startDate,endDate);

        assertEquals(0, tested.size());
    }

    @Test
    void testTopStudent() throws LibraryException {
        Student student = new Student("201830221",new Career(58L, "Sistemas"), "Willians");
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);

        List<LoanCountByStudent> loanCountByStudents = new ArrayList<>();
        loanCountByStudents.add(new LoanCountByStudent(5L, student));

        when(loanRepository.topStudent(startDate, endDate)).thenReturn(loanCountByStudents);
        when(loanRepository.findAllByStudentAndLoanDateBetween(student, startDate, endDate)).thenReturn(new ArrayList<>());

        TopStudentReportResponse response = loanService.topStudent(startDate, endDate);

        assertEquals(0, response.getLoanList().size());
        assertEquals(5L, response.getCount());
    }

    @Test
    void testTopStudentException() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 10);


        when(loanRepository.topStudent(startDate, endDate)).thenReturn(new ArrayList<>());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            loanService.topStudent(startDate, endDate);
        });

        assertEquals("No result for this report", libraryException.getMessage());
    }

    @Test
    void testLoansWithOverdue(){
        LocalDate todayDate = LocalDate.of(2024, 6, 1);
        when(loanRepository.loansWithOverdue(todayDate)).thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.loansWithOverdue(todayDate);

        assertEquals(0, tested.size());
    }

    @Test
    void testLoansForToday(){
        LocalDate todayDate = LocalDate.of(2024, 6, 1);
        when(loanRepository.findAllByExpectedDateAndReturnDateNull(todayDate)).thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.loansForToday(todayDate);

        assertEquals(0, tested.size());
    }

    @Test
    void testOverdueLoans(){
        LocalDate todayDate = LocalDate.of(2024, 6, 1);
        when(loanRepository.listLateLoans(todayDate)).thenReturn(new ArrayList<>());

        List<Loan> tested = loanService.overdueLoans(todayDate);

        assertEquals(0, tested.size());
    }
}
