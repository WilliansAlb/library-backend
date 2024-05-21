package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    public static final String LICENSE_STUDENT = "201830221";
    public static final String NAME_STUDENT = "Willians Alberto Orozco";
    public static final LocalDate BIRTHDAY = LocalDate.of(1998, Month.JULY, 21);
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void testFindAllStudents(){
        List<Student> studentList = new ArrayList<>();
        when(studentRepository.findAll()).thenReturn(studentList);

        List<Student> result = studentService.findAllStudents();

        assertNotNull(result);
    }

    @Test
    void testFindByLicenseAndNameAndBirthdaySuccessfully(){
        when(studentRepository.findByLicenseAndNameAndBirthdayAndUserLibraryIsNull(LICENSE_STUDENT, NAME_STUDENT, BIRTHDAY)).thenReturn(Optional.empty());

        Optional<Student> tested = studentService.findByLicenseAndNameAndBirthday(LICENSE_STUDENT, NAME_STUDENT, BIRTHDAY);

        assertNotNull(tested);
    }

    @Test
    void testHasPendingInvitation() throws LibraryException {
        Student saved = new Student();
        saved.setUserLibrary(null);
        saved.setLicense(LICENSE_STUDENT);
        saved.setName(NAME_STUDENT);
        saved.setBirthday(BIRTHDAY);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.of(saved));

        boolean tested = studentService.hasPendingInvitation(LICENSE_STUDENT);

        assertTrue(tested);

    }

    @Test
    void testHasNotPendingInvitation() throws LibraryException {
        Student saved = new Student();
        saved.setUserLibrary(new UserLibrary());
        saved.setLicense(LICENSE_STUDENT);
        saved.setName(NAME_STUDENT);
        saved.setBirthday(BIRTHDAY);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.of(saved));

        boolean tested = studentService.hasPendingInvitation(LICENSE_STUDENT);

        assertFalse(tested);

    }

    @Test
    void testHasPendingInvitationStudentNotExistsException()  {
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            studentService.hasPendingInvitation(LICENSE_STUDENT);
        });

        assertEquals("student_doesnt_exists",libraryException.getMessage());
    }

    @Test
    void testFindByLicense()  {
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.empty());

        Optional<Student> tested = studentService.findByLicense(LICENSE_STUDENT);

        assertNotNull(tested);
    }

    @Test
    void testSaveStudent(){
        Student saved = new Student();
        saved.setLicense(LICENSE_STUDENT);
        when(studentRepository.save(saved)).thenReturn(saved);

        Student tested = studentService.saveStudent(saved);

        assertEquals(LICENSE_STUDENT, tested.getLicense());
    }
}
