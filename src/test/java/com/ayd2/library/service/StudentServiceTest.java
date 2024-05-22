package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.bouncycastle.LICENSE;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    public static final String LICENSE_STUDENT = "201830221";
    public static final String LICENSE_STUDENT_INCORRECT = "201830221A";
    public static final String NAME_STUDENT = "Willians Alberto Orozco";
    public static final String FILTER_STUDENT = "test";
    public static final long ALL_CAREER_ID = -1L;
    public static final long CAREER_ID = 58L;
    public static final LocalDate BIRTHDAY = LocalDate.of(1998, Month.JULY, 21);
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CareerService careerService;

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

    @Test
    void testCreateStudentSuccessfully() throws LibraryException {
        Student toCreate = new Student();
        toCreate.setLicense(LICENSE_STUDENT);
        Career careerStudent = new Career();
        careerStudent.setCareerId(CAREER_ID);
        toCreate.setCareer(careerStudent);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.empty());
        when(careerService.findById(CAREER_ID)).thenReturn(Optional.of(careerStudent));
        when(studentRepository.save(toCreate)).thenReturn(toCreate);

        Student tested = studentService.createStudent(toCreate);

        assertNull(tested.getUserLibrary());
        verify(studentRepository).findById(LICENSE_STUDENT);
        verify(careerService).findById(CAREER_ID);
        verify(studentRepository).save(toCreate);
    }

    @Test
    void testCreateStudentBadFormLicenseException(){
        Student toCreate = new Student();
        toCreate.setLicense(LICENSE_STUDENT_INCORRECT);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            studentService.createStudent(toCreate);
        });

        assertEquals("Not valid form for license", libraryException.getMessage());
    }
    @Test
    void testCreateStudentExistsException(){
        Student toCreate = new Student();
        toCreate.setLicense(LICENSE_STUDENT);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.of(toCreate));

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            studentService.createStudent(toCreate);
        });

        assertEquals("The student exists", libraryException.getMessage());

        verify(studentRepository).findById(LICENSE_STUDENT);
    }
//Missing the career
    @Test
    void testCreateStudentMissingCareerException() {
        Student toCreate = new Student();
        toCreate.setLicense(LICENSE_STUDENT);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            studentService.createStudent(toCreate);
        });

        assertEquals("Missing the career", libraryException.getMessage());

        verify(studentRepository).findById(LICENSE_STUDENT);
    }

    @Test
    void testCreateStudentCareerNotExistsException() {
        Student toCreate = new Student();
        toCreate.setLicense(LICENSE_STUDENT);
        Career careerStudent = new Career();
        careerStudent.setCareerId(CAREER_ID);
        toCreate.setCareer(careerStudent);
        when(studentRepository.findById(LICENSE_STUDENT)).thenReturn(Optional.empty());
        when(careerService.findById(CAREER_ID)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            studentService.createStudent(toCreate);
        });

        assertEquals("The career doesnt exists", libraryException.getMessage());

        verify(studentRepository).findById(LICENSE_STUDENT);
        verify(careerService).findById(CAREER_ID);
    }

    @Test
    void testFilterStudentSuccessfullyBySearchWord(){
        List<Student> studentList = new ArrayList<>();
        when(studentRepository.findByLicenseOrNameContainingIgnoreCase(FILTER_STUDENT, FILTER_STUDENT)).thenReturn(studentList);

        List<Student> filterStudents = studentService.filterStudent(FILTER_STUDENT, CAREER_ID);

        assertNotNull(filterStudents);

        verify(studentRepository).findByLicenseOrNameContainingIgnoreCase(FILTER_STUDENT,FILTER_STUDENT);
    }

    @Test
    void testFilterStudentSuccessfullyNullSearchWord(){
        List<Student> studentList = new ArrayList<>();
        when(studentRepository.findAll()).thenReturn(studentList);

        List<Student> filterStudents = studentService.filterStudent(null, CAREER_ID);

        assertNotNull(filterStudents);
        verify(studentRepository).findAll();
    }

    @Test
    void testFilterStudentSuccessfullyNullEmptyWord(){
        List<Student> studentList = new ArrayList<>();
        when(studentRepository.findAll()).thenReturn(studentList);

        List<Student> filterStudents = studentService.filterStudent("", CAREER_ID);

        assertNotNull(filterStudents);
        verify(studentRepository).findAll();
    }

    @Test
    public void testFilterStudentWithoutSearchAndCareerId() {
        // Mocking repository behavior
        List<Student> mockStudents = Arrays.asList(
                new Student("201830221", new Career(1L, "Computer Science"), "Willians"),
                new Student("201830212", new Career(2L, "Mathematics"), "Fernanda")
        );
        when(studentRepository.findAll()).thenReturn(mockStudents);

        // Testing the method
        List<Student> result = studentService.filterStudent(null, 0L);

        // Assertion
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterStudentWithSearchAndCareerId() {
        List<Student> mockStudents = Arrays.asList(
                new Student("201830221", new Career(1L, "Computer Science"), "Willians"),
                new Student("201830212", new Career(2L, "Mathematics"), "Fernanda")
        );
        when(studentRepository.findByLicenseOrNameContainingIgnoreCase(FILTER_STUDENT, FILTER_STUDENT)).thenReturn(mockStudents);

        // Testing the method
        List<Student> result = studentService.filterStudent(FILTER_STUDENT, 1L);

        // Assertion
        assertEquals(1, result.size());
        assertEquals("Willians", result.get(0).getName());
    }

    @Test
    public void testFindByUserLibrary(){
        when(studentRepository.findByUserLibrary(any(UserLibrary.class))).thenReturn(Optional.empty());

        Optional<Student> optionalUserLibrary = studentService.findByUserLibrary(new UserLibrary());

        assertTrue(optionalUserLibrary.isEmpty());
    }

    @Test
    public void testFindByUserLibraryReturnsStudent(){
        when(studentRepository.findByUserLibrary(any(UserLibrary.class))).thenReturn(Optional.of(new Student()));

        Optional<Student> optionalUserLibrary = studentService.findByUserLibrary(new UserLibrary());

        assertTrue(optionalUserLibrary.isPresent());
    }
}
