package com.example.demo.application.user;

import com.example.demo.infrastructure.jpa.entity.UserEntity;

public interface UserService {
	UserEntity getUserData(String username);
}