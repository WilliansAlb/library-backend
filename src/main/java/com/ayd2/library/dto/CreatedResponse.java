package com.ayd2.library.dto;

import com.ayd2.library.model.Book;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatedResponse {
    private List<FileErrorResponse> errors;
    private List<Book> books;
    private List<Student> students;
    private List<Loan> loans;
    private List<Career> careers;
}
