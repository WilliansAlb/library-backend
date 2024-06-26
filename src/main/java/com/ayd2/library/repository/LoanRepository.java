package com.ayd2.library.repository;

import com.ayd2.library.dto.LoanCountByStudent;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import com.ayd2.library.dto.LoanCountByCareer;
import com.ayd2.library.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByStudentAndReturnDateNull(Student student);

    List<Loan> findAllByStudentAndLatePaymentNotNullAndReturnDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    List<Loan> findAllByStudentCareerAndLoanDateBetween(Career student_career, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.ayd2.library.dto.LoanCountByCareer(COUNT(loan.loanId), loan.student.career) " +
            "FROM Loan loan " +
            "WHERE loan.loanDate " +
            "BETWEEN :startDate AND :endDate " +
            "GROUP BY loan.student.career " +
            "ORDER BY COUNT(loan.loanId) DESC")
    List<LoanCountByCareer> getCareerTopLoans(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Loan> findAllByExpectedDateAndReturnDateNull(LocalDate todayDate);

    List<Loan> findAllByExpectedDateBeforeAndReturnDateNull(LocalDate todayDate);

    @Query("SELECT SUM(loan.loanPayment) " +
            "FROM Loan loan " +
            "WHERE loan.loanDate BETWEEN :startDate AND :endDate ")
    BigDecimal getNormalTotal(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(loan.latePayment) " +
            "FROM Loan loan " +
            "WHERE loan.loanDate BETWEEN :startDate AND :endDate ")
    BigDecimal getLateTotal(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Loan> findAllByLoanDateBetween(LocalDate startDate, LocalDate endDate);

    List<Loan> findAllByStudentAndLoanDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT * From loan l " +
            "WHERE l.student = :license AND " +
            "(:todayDate - l.expected_date) > 30 AND l.return_date IS NULL", nativeQuery = true)
    List<Loan> listOverdueLoans(@Param("license") String license, @Param("todayDate") LocalDate todayDate);

    @Query(value = "SELECT * From loan l " +
            "WHERE l.expected_date < :todayDate  AND l.return_date IS NULL", nativeQuery = true)
    List<Loan> listLateLoans(@Param("todayDate") LocalDate todayDate);

    @Query("SELECT new com.ayd2.library.dto.LoanCountByStudent(COUNT(loan.loanId), loan.student) " +
            "FROM Loan loan " +
            "WHERE loan.loanDate " +
            "BETWEEN :startDate AND :endDate " +
            "GROUP BY loan.student " +
            "ORDER BY COUNT(loan.loanId) DESC")
    List<LoanCountByStudent> topStudent(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * From loan l " +
            "WHERE (:todayDate - l.expected_date) > 30 AND l.return_date IS NULL", nativeQuery = true)
    List<Loan> loansWithOverdue(@Param("todayDate") LocalDate todayDate);
}
