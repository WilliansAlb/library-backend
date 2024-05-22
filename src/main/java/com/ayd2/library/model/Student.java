package com.ayd2.library.model;

import com.ayd2.library.util.LibraryConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student {
    @Id
    @Column(name = "license")
    private String license;

    @JoinColumn(name = "user_library")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserLibrary userLibrary;

    @JoinColumn(name = "career")
    @ManyToOne(fetch = FetchType.LAZY)
    private Career career;

    @Column(name = "name")
    private String name;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "birthday")
    private LocalDate birthday;

    public Student(String license, Career career, String name){
        this.license = license;
        this.career = career;
        this.name = name;
    }

    public Student(){

    }
}
