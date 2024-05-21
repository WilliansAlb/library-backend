package com.ayd2.library.model;

import com.ayd2.library.util.LibraryConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan")
@Getter
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanIdGenerator")
    @SequenceGenerator(name = "loanIdGenerator", sequenceName = "SEQ_LOAN", initialValue = 1, allocationSize = 1)
    @Column(name = "loan_id")
    private Long loanId;

    @JoinColumn(name = "book")
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @JoinColumn(name = "student")
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "loan_date")
    private LocalDate loanDate;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "loan_payment")
    private BigDecimal loanPayment;

    @Column(name = "late_payment")
    private BigDecimal latePayment;

    @Column(name = "penalty_payment")
    private boolean penaltyPayment;

}
