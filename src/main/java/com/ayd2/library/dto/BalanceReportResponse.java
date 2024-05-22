package com.ayd2.library.dto;

import com.ayd2.library.model.Loan;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BalanceReportResponse {
    List<Loan> loanList;
    BigDecimal totalNormal;
    BigDecimal totalLate;
}
