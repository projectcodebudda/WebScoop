package com.example.demo.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.infrastructure.jpa.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByUserName(String userName);
}
