package com.ayd2.library.dto;

import com.ayd2.library.model.Career;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanCountByCareer {
    private Long loanCount;
    private Career career;

    public LoanCountByCareer(Long loanCount, Career career) {
        this.career = career;
        this.loanCount = loanCount;
    }
}
