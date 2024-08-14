package com.example.demo.infrastructure.login;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.login.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
