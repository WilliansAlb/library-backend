package com.ayd2.library.repository;

import com.ayd2.library.model.UserLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserLibrary, Long> {

    Optional<UserLibrary> findByUsername(String username);

    @Query(value = "SELECT user FROM UserLibrary user WHERE user.username = :username AND user.userId <> :id")
    Optional<UserLibrary> findDuplicatedByUsernameAndNotId(@Param("username") String email, @Param("id") Long id);
}
