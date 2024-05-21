package com.ayd2.library.dto;

import com.ayd2.library.model.Loan;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BalanceLoanRequest {
    private Loan loan;
    private LocalDate todayDate;
}
