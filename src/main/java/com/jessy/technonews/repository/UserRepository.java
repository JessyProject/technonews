package com.jessy.technonews.repository;

import java.util.Optional;

import com.jessy.technonews.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    Optional<User> findByUsername(String username);
    boolean existsUserByEmail(String email);
    boolean existsUserByUsername(String username);
}
