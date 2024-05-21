package com.ayd2.library.controller;

import com.ayd2.library.dto.UserStudentRequest;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    public static final String USERNAME_USER = "williansAlb";
    public static final String LICENSE_STUDENT = "201830221";
    public static final String PASSWORD_USER = "testPassword";
    public static final String NAME_STUDENT = "Willians Alberto Orozco";
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<UserLibrary> userList = Arrays.asList(new UserLibrary(), new UserLibrary());
        when(userService.findAll()).thenReturn(userList);

        ResponseEntity<List<UserLibrary>> response = userController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
        verify(userService, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        UserLibrary user = new UserLibrary();
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<UserLibrary> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserLibrary> response = userController.findById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testCreate() throws LibraryException {
        UserLibrary user = new UserLibrary();
        when(userService.create(user)).thenReturn(user);

        ResponseEntity<UserLibrary> response = userController.create(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).create(user);
    }

    @Test
    void testUpdate() throws LibraryException {
        UserLibrary user = new UserLibrary();
        when(userService.update(1L, user)).thenReturn(user);

        ResponseEntity<UserLibrary> response = userController.update(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).update(1L, user);
    }

    @Test
    void testCreateStudent() throws LibraryException {
        Student returned = new Student();
        returned.setName(NAME_STUDENT);
        returned.setLicense(LICENSE_STUDENT);
        UserStudentRequest request = new UserStudentRequest();
        request.setUsername(USERNAME_USER);
        request.setName(NAME_STUDENT);
        request.setPassword(PASSWORD_USER);
        request.setLicense(LICENSE_STUDENT);
        when(userService.createStudent(request)).thenReturn(returned);

        ResponseEntity<Student> response = userController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(returned, response.getBody());
        verify(userService, times(1)).createStudent(request);
    }
}
