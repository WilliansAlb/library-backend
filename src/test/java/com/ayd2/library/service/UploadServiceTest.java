package com.ayd2.library.service;

import com.ayd2.library.dto.CreatedResponse;
import com.ayd2.library.dto.LoanStudentRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private StudentService studentService;

    @Mock
    private CareerService careerService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private UploadService uploadService;

    @Test
    public void testReadFileSuccess() throws LibraryException {
        String fileContent = "LIBRO\nTITULO:Example Book\nAUTOR:Author Name\nCODIGO:123-ABC\nCANTIDAD:5\n" +
                "ESTUDIANTE\nCARNET:20210001\nNOMBRE:John Doe\nCARRERA:1\n" +
                "PRESTAMO\nCODIGOLIBRO:123-ABC\nCARNET:20210001\nFECHA:2023-05-20\n" +
                "CARRERA\nCODIGO:1\nNOMBRE:Computer Science\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        when(bookService.createBook(any(Book.class))).thenReturn(new Book());
        when(careerService.saveCareer(any(Career.class))).thenReturn(new Career());
        when(studentService.createStudent(any(Student.class))).thenReturn(new Student());
        when(loanService.createLoans(any(LoanStudentRequest.class))).thenReturn(new ArrayList<>());
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(1, response.getBooks().size());
        assertEquals(1, response.getStudents().size());
        assertEquals(1, response.getLoans().size());
        assertEquals(1, response.getCareers().size());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    public void testReadFileSuccess2() throws LibraryException {
        String fileContent = "LIBRO\nTITULO:Example Book\nAUTOR:Author Name\n  \n  \nCODIGO:123-ABC\nCANTIDAD:5\n" +
                "ESTUDIANTE\nCARNET:20210001\nNOMBRE:John Doe\n\t\nCARRERA:1\n" +
                "PRESTAMO\nCODIGOLIBRO:123-ABC\nCARNET:20210001\n \nFECHA:2023-05-20\n" +
                "CARRERA\nCODIGO:1\n  \nNOMBRE:Computer Science\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        when(bookService.createBook(any(Book.class))).thenReturn(new Book());
        when(careerService.saveCareer(any(Career.class))).thenReturn(new Career());
        when(studentService.createStudent(any(Student.class))).thenReturn(new Student());
        when(loanService.createLoans(any(LoanStudentRequest.class))).thenReturn(new ArrayList<>());
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(1, response.getBooks().size());
        assertEquals(1, response.getStudents().size());
        assertEquals(1, response.getLoans().size());
        assertEquals(1, response.getCareers().size());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    public void testReadFileWithInvalidISBN() throws LibraryException {
        String fileContent = "LIBRO\nTITULO:Example Book\nAUTOR:Author Name\nCODIGO:INVALID-ISBN\nCANTIDAD:5\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getBooks().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("CODIGO no es un codigo valido", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileWithInvalidCopies() throws LibraryException {
        String fileContent = "LIBRO\nTITULO:Example Book\nAUTOR:Author Name\nCODIGO:123-ISB\nCANTIDAD:5a\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getBooks().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("CANTIDAD no es un número válido", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileWithInvalidCareerId() throws LibraryException {
        String fileContent = "ESTUDIANTE\nCARNET:201830221\nNOMBRE:Willians Alberto\nCARRERA:58a\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getStudents().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("CARRERA no es un número válido", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileWithInvalidBookIdLoan() throws LibraryException {
        String fileContent = "PRESTAMO\nCODIGOLIBRO:101-DOYa\nCARNET:201830221\nFECHA:2024-04-23\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getLoans().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("CODIGOLIBRO no es un codigo valido", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileWithInvalidDateLoan() throws LibraryException {
        String fileContent = "PRESTAMO\nCODIGOLIBRO:101-DOY\nCARNET:201830221\nFECHA:2024-04-23AA\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getLoans().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("FECHA no es una fecha valida", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileWithInvalidCareer() throws LibraryException {
        String fileContent = "CARRERA\nCODIGO:58a\nNOMBRE:Ingeniería en Sistemasñ\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getCareers().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("CODIGO no es un número válido", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileCreateBookWithLibraryException() throws LibraryException {
        String fileContent = "LIBRO\nTITULO:Example Book\nAUTOR:Author Name\nCODIGO:123-ABC\nCANTIDAD:5\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        doThrow(new LibraryException("Library Exception")).when(bookService).createBook(any(Book.class));

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getBooks().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("Library Exception", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileCreateCareerWithLibraryException() throws LibraryException {
        String fileContent = "CARRERA\nCODIGO:58\nNOMBRE:Ingeniería en Sistemasñ\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        doThrow(new LibraryException("Library Exception")).when(careerService).saveCareer(any(Career.class));

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getCareers().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("Library Exception", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileCreateStudentWithLibraryException() throws LibraryException {
        String fileContent = "ESTUDIANTE\nCARNET:201830221\nNOMBRE:Willians Alberto\nCARRERA:58\n";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        doThrow(new LibraryException("Library Exception")).when(studentService).createStudent(any(Student.class));

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getStudents().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("Library Exception", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileCreateLoanWithLibraryException() throws LibraryException {
        String fileContent = "PRESTAMO\nCODIGOLIBRO:101-DOY\nCARNET:201830221\nFECHA:2024-04-23";
        BufferedReader bufferedReader = createBufferedReader(fileContent);
        LocalDate todayDate = LocalDate.now();

        doThrow(new LibraryException("Library Exception")).when(loanService).createLoans(any(LoanStudentRequest.class));

        CreatedResponse response = uploadService.readFile(bufferedReader, todayDate);

        assertEquals(0, response.getLoans().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("Library Exception", response.getErrors().get(0).getReason());
    }

    @Test
    public void testReadFileIOException() throws IOException {
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenThrow(new IOException("Test IOException"));

        LocalDate todayDate = LocalDate.now();

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            uploadService.readFile(bufferedReader, todayDate);
        });

        assertEquals("Error at read file", libraryException.getMessage());
    }

    private BufferedReader createBufferedReader(String content) {
        return new BufferedReader(new StringReader(content));
    }
}
