package com.ayd2.library.dto;

import com.ayd2.library.util.LibraryConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserStudentRequest {
    private String license;
    private String username;
    private String password;
    private String name;
    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;
}
