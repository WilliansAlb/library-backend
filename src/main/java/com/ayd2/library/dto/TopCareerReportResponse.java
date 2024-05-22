package com.ayd2.library.dto;

import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopCareerReportResponse {
    private Long count;
    private Career career;
    private List<Loan> loanList;
}
