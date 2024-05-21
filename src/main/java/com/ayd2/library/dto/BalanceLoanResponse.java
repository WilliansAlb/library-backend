package com.ayd2.library.dto;

import com.ayd2.library.model.Loan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceLoanResponse {
    private Loan loan;
    private double totalLate;
    private double totalNormal;
    private double sanction;
}
