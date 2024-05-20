package com.ayd2.library.model;

import com.ayd2.library.util.LibraryConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book {

    @Id
    @Column(name = "isbn")
    private String isbn;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "published")
    private LocalDate published;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "copies")
    private int copies;

    @Column(name = "available_copies")
    private int availableCopies;

    @Column(name = "front_cover")
    private String frontCover;

    @Column(name = "back_cover")
    private String backCover;

    @Column(name = "spine")
    private String spine;
}
