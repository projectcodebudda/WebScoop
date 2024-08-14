package com.example.demo.domain.login;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
	public Object getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

    // Getters and Setters
}