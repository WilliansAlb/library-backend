package com.ayd2.library.dto;

import com.ayd2.library.model.Book;
import com.ayd2.library.model.Student;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class LoanStudentRequest {
    private List<Book> bookList;
    private Student student;
    private LocalDate loanDate;
    private LocalDate expectedDate;
    private LocalDate todayDate;
}
