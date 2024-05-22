package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.UserRepository;
import com.ayd2.library.util.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        LibraryException thrown = assertThrows(LibraryException.class, () -> {
            userDetailService.loadUserByUsername(username);
        });

        assertEquals("user_by_username_not_found", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserFoundAsStudent() {
        String username = "studentUser";
        UserLibrary userLibrary = new UserLibrary();
        userLibrary.setUsername(username);
        userLibrary.setPassword("password");
        userLibrary.setStudent(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userLibrary));

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(RoleEnum.STUDENT.roleId)));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserFoundAsLibrarian() {
        String username = "librarianUser";
        UserLibrary userLibrary = new UserLibrary();
        userLibrary.setUsername(username);
        userLibrary.setPassword("password");
        userLibrary.setStudent(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userLibrary));

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(RoleEnum.LIBRARIAN.roleId)));

        verify(userRepository, times(1)).findByUsername(username);
    }
}
