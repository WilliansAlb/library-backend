package com.ayd2.library.dto;

import com.ayd2.library.model.Book;
import com.ayd2.library.model.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private Student student;
    private Book book;
}
