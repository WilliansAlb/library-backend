package com.ayd2.library.dto;

import com.ayd2.library.model.Book;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanStudentResponse {
    private Book toLoan;
    private boolean isLended;
    private String reasonForNotLend;
}
