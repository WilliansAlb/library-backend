package com.ayd2.library.service;

import com.ayd2.library.dto.CreatedResponse;
import com.ayd2.library.dto.FileErrorResponse;
import com.ayd2.library.dto.LoanStudentRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Loan;
import com.ayd2.library.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final BookService bookService;
    private final StudentService studentService;
    private final CareerService careerService;
    private final LoanService loanService;

    public CreatedResponse readFile(BufferedReader bufferedReader, LocalDate todayDate) throws LibraryException {
        List<Book> books = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Loan> loans = new ArrayList<>();
        List<Career> careers = new ArrayList<>();
        List<FileErrorResponse> errors = new ArrayList<>();
        Pattern ISBNPattern = Pattern.compile("^\\d{3}-[A-Z]{3}$");
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        try (BufferedReader br = bufferedReader) {
            String line;
            String currentSection = "";
            String title = "", author = "", code = "", license = "", name = "", date = "";
            int copies = 0, career = 0;
            int currentLine = 0;
            while ((line = br.readLine()) != null) {
                currentLine++;
                switch (line) {
                    case "LIBRO":
                        currentSection = "LIBRO";
                        break;
                    case "ESTUDIANTE":
                        currentSection = "ESTUDIANTE";
                        break;
                    case "PRESTAMO":
                        currentSection = "PRESTAMO";
                        break;
                    case "CARRERA":
                        currentSection = "CARRERA";
                        break;
                    default:
                        if (currentSection.equals("LIBRO")) {
                            if (line.startsWith("TITULO:")) {
                                title = line.substring(7).trim();
                            } else if (line.startsWith("AUTOR:")) {
                                author = line.substring(6).trim();
                            } else if (line.startsWith("CODIGO:")) {
                                code = line.substring(7).trim();
                                if (!isValid(code, ISBNPattern)){
                                    errors.add(new FileErrorResponse(currentLine, "CODIGO no es un codigo valido"));
                                    code = "";
                                }
                            } else if (line.startsWith("CANTIDAD:")) {
                                try {
                                    copies = Integer.parseInt(line.substring(9).trim());
                                } catch (NumberFormatException e) {
                                    errors.add(new FileErrorResponse(currentLine, "CANTIDAD no es un número válido"));
                                    copies = 0;
                                }
                            }
                        } else if (currentSection.equals("ESTUDIANTE")) {
                            if (line.startsWith("CARNET:")) {
                                license = line.substring(7).trim();
                            } else if (line.startsWith("NOMBRE:")) {
                                name = line.substring(7).trim();
                            } else if (line.startsWith("CARRERA:")) {
                                try {
                                    career = Integer.parseInt(line.substring(8).trim());
                                } catch (NumberFormatException e) {
                                    errors.add(new FileErrorResponse(currentLine, "CARRERA no es un número válido"));
                                    career = 0;
                                }
                            }
                        } else if (currentSection.equals("PRESTAMO")) {
                            if (line.startsWith("CODIGOLIBRO:")) {
                                code = line.substring(12).trim();
                                if (!isValid(code, ISBNPattern)){
                                    errors.add(new FileErrorResponse(currentLine, "CODIGOLIBRO no es un codigo valido"));
                                    code = "";
                                }
                            } else if (line.startsWith("CARNET:")) {
                                license = line.substring(7).trim();
                            } else if (line.startsWith("FECHA:")) {
                                date = line.substring(6).trim();
                                if (!isValid(date, datePattern)){
                                    errors.add(new FileErrorResponse(currentLine, "FECHA no es una fecha valida"));
                                    date = "";
                                }
                            }
                        } else if (currentSection.equals("CARRERA")) {
                            if (line.startsWith("CODIGO:")) {
                                try {
                                    career = Integer.parseInt(line.substring(7).trim());
                                } catch (NumberFormatException e) {
                                    errors.add(new FileErrorResponse(currentLine, "CODIGO no es un número válido"));
                                    career = 0;
                                }
                            } else if (line.startsWith("NOMBRE:")) {
                                name = line.substring(7).trim();
                            }
                        }

                        if (currentSection.equals("LIBRO") && !title.isEmpty() && !author.isEmpty() && !code.isEmpty() && copies > 0) {
                            Book book = new Book();
                            book.setIsbn(code);
                            book.setTitle(title);
                            book.setAuthor(author);
                            book.setCopies(copies);
                            book.setAvailableCopies(copies);
                            books.add(book);
                            title = author = code = "";
                            copies = 0;
                        } else if (currentSection.equals("ESTUDIANTE") && !license.isEmpty() && !name.isEmpty() && career > 0) {
                            Student student = new Student();
                            student.setLicense(license);
                            student.setName(name);
                            Career temp = new Career();
                            temp.setCareerId((long) career);
                            student.setCareer(temp);
                            students.add(student);
                            license = name = "";
                            career = 0;
                        } else if (currentSection.equals("PRESTAMO") && !code.isEmpty() && !license.isEmpty() && !date.isEmpty()) {
                            Loan loan = new Loan();
                            loan.setLoanDate(LocalDate.parse(date));
                            Book book = new Book();
                            book.setIsbn(code);
                            Student student = new Student();
                            student.setLicense(license);
                            loan.setBook(book);
                            loan.setStudent(student);
                            loans.add(loan);
                            code = license = date = "";
                        } else if (currentSection.equals("CARRERA") && career > 0 && !name.isEmpty()) {
                            Career newCareer = new Career();
                            newCareer.setCareerId((long) career);
                            newCareer.setName(name);
                            careers.add(newCareer);
                            career = 0;
                            name = "";
                        }
                        break;
                }
            }
            CreatedResponse response = new CreatedResponse();
            response.setBooks(new ArrayList<>());
            response.setLoans(new ArrayList<>());
            response.setCareers(new ArrayList<>());
            response.setStudents(new ArrayList<>());
            for(Book toSave: books) {
                try {
                    bookService.createBook(toSave);
                    response.getBooks().add(toSave);
                } catch (LibraryException libraryException) {
                    errors.add(new FileErrorResponse(-1, libraryException.getMessage()));
                }
            }
            for (Career toSave: careers) {
                try {
                    careerService.saveCareer(toSave);
                    response.getCareers().add(toSave);
                } catch (LibraryException libraryException) {
                    errors.add(new FileErrorResponse(-2, libraryException.getMessage()));
                }
            }
            for(Student toSave: students) {
                try {
                    studentService.createStudent(toSave);
                    response.getStudents().add(toSave);
                } catch (LibraryException libraryException) {
                    errors.add(new FileErrorResponse(-3, libraryException.getMessage()));
                }
            }
            for (Loan toSave: loans) {
                try {
                    LoanStudentRequest request = new LoanStudentRequest();
                    request.setStudent(toSave.getStudent());
                    List<Book> temp = new ArrayList<>();
                    temp.add(toSave.getBook());
                    request.setBookList(temp);
                    request.setLoanDate(toSave.getLoanDate());
                    request.setTodayDate(todayDate);
                    loanService.createLoans(request);
                    response.getLoans().add(toSave);
                } catch (LibraryException libraryException) {
                    errors.add(new FileErrorResponse(-4, libraryException.getMessage()));
                }
            }
            response.setErrors(errors);
            return response;
        } catch (IOException e) {
            throw new LibraryException("Error at read file");
        }
    }

    private static boolean isValid(String toValidate, Pattern pattern) {
        if (toValidate == null) return false;
        Matcher matcher = pattern.matcher(toValidate);
        return matcher.matches();
    }
}
