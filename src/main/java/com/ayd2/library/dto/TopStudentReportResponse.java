package com.ayd2.library.dto;

import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopStudentReportResponse {
    private Long count;
    private Student student;
    private List<Loan> loanList;
}
