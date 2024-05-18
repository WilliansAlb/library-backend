package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new LibraryException("user_by_username_not_found").status(HttpStatus.NOT_FOUND);
        var user = userOpt.get();
        var role = user.isStudent()?"student":"librarian";
        var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
