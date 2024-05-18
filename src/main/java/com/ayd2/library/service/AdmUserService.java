package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdmUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserLibrary> findById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setPassword(null);
                    return Optional.of(user);
                })
                .orElse(Optional.empty());
    }

    public List<UserLibrary> findAll() {
        return userRepository.findAll();
    }

    public UserLibrary create(UserLibrary entity) throws LibraryException {
        var userByUsername = userRepository.findByUsername(entity.getUsername());
        if (userByUsername.isPresent()) throw new LibraryException("username_already_exists");
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return userRepository.save(entity);
    }

    public UserLibrary update(Long userId, UserLibrary entity) throws LibraryException {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) throw new LibraryException("user_not_found")
                .status(HttpStatus.NOT_FOUND);

        if (!entity.getUserId().equals(userId)) throw new LibraryException("invalid_update");
        var duplicatedEmail = userRepository.findDuplicatedByUsernameAndNotId(entity.getUsername(), userId);
        if (duplicatedEmail.isPresent()) throw new LibraryException("username_already_exists");

        entity.setPassword(userOpt.get().getPassword());
        return userRepository.save(entity);
    }
}
