package com.example.demo.domain.user;

import java.time.Instant;

import com.example.demo.domain.common.DomainEntity;
import com.example.demo.domain.user.login.LoginInfo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@DomainEntity
public class User {
	private LoginInfo loginInfo;
	private Instant createdAt;
	private Instant updatedAt;
	
	
}