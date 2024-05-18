CREATE TABLE user_library (
    user_id        BIGINT NOT NULL,
    username  VARCHAR(15) NOT NULL,
    password  VARCHAR(500) NOT NULL,
    is_student BOOLEAN NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (user_id)
);

CREATE TABLE career (
    career_id      BIGINT NOT NULL,
    "name"  VARCHAR(100) NOT NULL,
    CONSTRAINT career_pk PRIMARY KEY (career_id)
);

CREATE TABLE book (
    isbn        VARCHAR(13) NOT NULL,
    author      VARCHAR(100) NOT NULL,
    title       VARCHAR(200) NOT NULL,
    published   DATE NOT NULL,
    publisher   VARCHAR(200) NOT NULL,
    copies      BIGINT NOT NULL,
    front_cover VARCHAR NOT NULL,
    back_cover  VARCHAR NOT NULL,
    spine       VARCHAR NOT NULL,
    CONSTRAINT book_pk PRIMARY KEY (isbn)
);

CREATE TABLE loan (
    loan_id              BIGINT NOT NULL,
    book            VARCHAR(13) NOT NULL,
    student         VARCHAR(9) NOT NULL,
    loan_date       DATE NOT NULL,
    expected_date   DATE,
    return_date     DATE,
    loan_payment    NUMERIC(20, 6),
    late_payment    NUMERIC(20, 6),
    penalty_payment BOOLEAN,
    CONSTRAINT loan_pk PRIMARY KEY (loan_id)
);

CREATE TABLE student (
    license      VARCHAR(9) NOT NULL,
    user_library       BIGINT UNIQUE,
    career       BIGINT NOT NULL,
    name         VARCHAR(100),
    birthday     DATE,
    CONSTRAINT student_pk PRIMARY KEY (license)
);

CREATE TABLE booking (
    booking_id              BIGINT NOT NULL,
    book            VARCHAR(13) NOT NULL,
    student         VARCHAR(9) NOT NULL,
    release_date    DATE,
    limit_date      DATE,
    showed          BOOLEAN DEFAULT false,
    CONSTRAINT booking_pk PRIMARY KEY (booking_id)
);

ALTER TABLE student
ADD CONSTRAINT user_student_fk
FOREIGN KEY (user_library)
REFERENCES user_library (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE student
    ADD CONSTRAINT career_student_fk
        FOREIGN KEY (career)
            REFERENCES career (career_id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
            NOT DEFERRABLE;

ALTER TABLE loan
    ADD CONSTRAINT loan_book_fk
        FOREIGN KEY (book)
            REFERENCES book (isbn)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
            NOT DEFERRABLE;

ALTER TABLE loan
    ADD CONSTRAINT loan_student_fk
        FOREIGN KEY (student)
            REFERENCES student (license)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
            NOT DEFERRABLE;

ALTER TABLE booking
    ADD CONSTRAINT booking_book_fk
        FOREIGN KEY (book)
            REFERENCES book (isbn)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
            NOT DEFERRABLE;

ALTER TABLE booking
    ADD CONSTRAINT booking_student_fk
        FOREIGN KEY (student)
            REFERENCES student (license)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
            NOT DEFERRABLE;
