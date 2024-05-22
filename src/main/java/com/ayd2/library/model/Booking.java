package com.ayd2.library.model;

import com.ayd2.library.util.LibraryConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookingIdGenerator")
    @SequenceGenerator(name = "bookingIdGenerator", sequenceName = "SEQ_BOOKING", initialValue = 1, allocationSize = 1)
    @Column(name = "booking_id")
    private Long bookingId;

    @JoinColumn(name = "book")
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @JoinColumn(name = "student")
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @JsonFormat(pattern = LibraryConstant.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Column(name = "limit_date")
    private LocalDate limitDate;

    private boolean showed;
}
