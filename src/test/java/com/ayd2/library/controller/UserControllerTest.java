package com.ayd2.library.controller;

import com.ayd2.library.exception.LibraryException;
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
}
