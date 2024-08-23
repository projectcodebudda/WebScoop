package com.example.demo.application.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.demo.infrastructure.jpa.entity.UserEntity;
import com.example.demo.infrastructure.jpa.repository.UserJpaRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserJpaRepository repository;

    @PostConstruct
    public void init() {
    	
    }
    
    @Override	
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
        	UserEntity userData = this.repository.findByUserName(username);
        	
        	Assert.notNull(userData, "계정이 없습니다.");
        	
            List<GrantedAuthority> authList = new ArrayList<>();
            authList.add(new SimpleGrantedAuthority("ADMIN"));
            
            return new User(userData.getUserName(), userData.getPassword(), true, true, true, true, authList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}