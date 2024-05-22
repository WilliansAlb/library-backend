package com.ayd2.library.dto;

import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanCountByStudent {
    private Long loanCount;
    private Student student;

    public LoanCountByStudent(Long loanCount, Student student) {
        this.student = student;
        this.loanCount = loanCount;
    }
}
