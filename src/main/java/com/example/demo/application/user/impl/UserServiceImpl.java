package com.example.demo.application.user.impl;

import org.springframework.stereotype.Service;

import com.example.demo.application.user.UserService;
import com.example.demo.infrastructure.jpa.entity.UserEntity;
import com.example.demo.infrastructure.jpa.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserJpaRepository repository;

	@Override
	public UserEntity getUserData(String username) {
		return this.repository.findByUserName(username);
	}

}