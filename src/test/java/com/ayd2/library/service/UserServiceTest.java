package com.ayd2.library.service;

import com.ayd2.library.dto.UserStudentRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    public static final long ID_USER = 1L;
    public static final String USERNAME_USER = "testUser";
    public static final String LICENSE_STUDENT = "201830221";
    public static final String NAME_STUDENT = "testName";
    public static final String NAME_CAREER = "testCareer";
    public static final LocalDate BIRTHDAY = LocalDate.of(1998, Month.JULY, 21);
    public static final String PASSWORD_USER = "testPassword";
    public static final boolean IS_STUDENT = true;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentService studentService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIdPresent(){
        UserLibrary test = new UserLibrary();
        test.setPassword(PASSWORD_USER);
        test.setUserId(ID_USER);
        test.setStudent(IS_STUDENT);
        test.setUsername(USERNAME_USER);
        when(userRepository.findById(ID_USER)).thenReturn(Optional.of(test));

        Optional<UserLibrary> user = userService.findById(ID_USER);

        assertTrue(user.isPresent());
        assertNull(user.get().getPassword());
    }

    @Test
    void testFindByIdEmpty(){
        when(userRepository.findById(ID_USER)).thenReturn(Optional.empty());
        Optional<UserLibrary> user = userService.findById(ID_USER);
        assertFalse(user.isPresent());
    }

    @Test
    void testFindAll(){
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<UserLibrary> list = userService.findAll();
        assertNotNull(list);
    }

    @Test
    void testCreate() throws LibraryException {
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        UserLibrary result = userService.create(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(passwordEncoder, times(1)).encode(PASSWORD_USER);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUsernameExists(){
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);
        user.setUserId(ID_USER);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        LibraryException exception = assertThrows(LibraryException.class, () -> {
           userService.create(user);
        });

        assertEquals("username_already_exists", exception.getMessage());
    }

    @Test
    void testUpdateCorrect() throws LibraryException {
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);
        user.setUserId(ID_USER);

        when(userRepository.findById(ID_USER)).thenReturn(Optional.of(user));
        when(userRepository.findDuplicatedByUsernameAndNotId(USERNAME_USER, ID_USER)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        assertNotNull(userService.update(ID_USER, user));

        verify(userRepository).findById(ID_USER);
        verify(userRepository).findDuplicatedByUsernameAndNotId(USERNAME_USER, ID_USER);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateNotFoundException() {
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);
        user.setUserId(ID_USER);

        when(userRepository.findById(ID_USER)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
           userService.update(ID_USER, user);
        });

        assertEquals("user_not_found", libraryException.getMessage());

        verify(userRepository).findById(ID_USER);
    }

    @Test
    void testUpdateInvalidException() {
        long different_id = 15L;
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);
        user.setUserId(different_id);

        when(userRepository.findById(ID_USER)).thenReturn(Optional.of(user));

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            userService.update(ID_USER, user);
        });

        assertEquals("invalid_update", libraryException.getMessage());

        verify(userRepository).findById(ID_USER);
    }

    @Test
    void testUpdateUsernameExists() {
        UserLibrary user = new UserLibrary();
        user.setUsername(USERNAME_USER);
        user.setStudent(IS_STUDENT);
        user.setPassword(PASSWORD_USER);
        user.setUserId(ID_USER);

        when(userRepository.findById(ID_USER)).thenReturn(Optional.of(user));
        when(userRepository.findDuplicatedByUsernameAndNotId(USERNAME_USER, ID_USER)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            userService.update(ID_USER, user);
        });

        assertEquals("username_already_exists", libraryException.getMessage());

        verify(userRepository).findById(ID_USER);
        verify(userRepository).findDuplicatedByUsernameAndNotId(USERNAME_USER, ID_USER);
    }

    @Test
    public void testCreateStudentSuccessfully() throws LibraryException {
        // Arrange
        UserStudentRequest request = new UserStudentRequest();
        request.setLicense(LICENSE_STUDENT);
        request.setName(NAME_STUDENT);
        request.setBirthday(BIRTHDAY);
        Student student = new Student();
        student.setLicense(LICENSE_STUDENT);
        student.setName(NAME_STUDENT);
        student.setBirthday(BIRTHDAY);
        student.setUserLibrary(null);
        UserLibrary userLibrary = new UserLibrary();
        userLibrary.setStudent(true);
        userLibrary.setUsername(USERNAME_USER);
        userLibrary.setPassword(PASSWORD_USER);
        Student created = new Student();
        created.setLicense(LICENSE_STUDENT);
        created.setName(NAME_STUDENT);
        created.setBirthday(BIRTHDAY);
        created.setUserLibrary(userLibrary);

        when(studentService.findByLicenseAndNameAndBirthday(request.getLicense(), request.getName(), request.getBirthday()))
                .thenReturn(Optional.of(student));
        when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserLibrary.class))).thenReturn(userLibrary);
        when(studentService.saveStudent(student)).thenReturn(created);

        Student tested = userService.createStudent(request);

        assertNull(tested.getUserLibrary().getPassword());
        assertTrue(tested.getUserLibrary().isStudent());
    }

    @Test
    public void testCreateStudentNotExistException() {
        // Arrange
        UserStudentRequest request = new UserStudentRequest();
        request.setLicense("license");
        request.setName("name");
        request.setBirthday(BIRTHDAY);

        when(studentService.findByLicenseAndNameAndBirthday(request.getLicense(), request.getName(), request.getBirthday()))
                .thenReturn(Optional.empty());

        // Act & Assert
        LibraryException exception = assertThrows(LibraryException.class, () -> {
            userService.createStudent(request);
        });

        assertEquals("student_doesnt_exist", exception.getMessage());

        verify(studentService).findByLicenseAndNameAndBirthday(request.getLicense(), request.getName(), request.getBirthday());
        verify(studentService, never()).saveStudent(any(Student.class));
    }
}
