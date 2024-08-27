package com.example.demo.presentation.view;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.application.user.UserService;
import com.example.demo.infrastructure.jpa.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {
	
	private final UserService userService;
	
	@GetMapping("/")
	public String getMainPage(Model model, Authentication authentication) {
		UserEntity user = this.userService.getUserData(authentication.getName());
		model.addAttribute("user", user);
		
		return "index";
	}
	
	@GetMapping("/login_page")
	public String getLoginPage(Model model) {
		return "signin";
	}
	
	@GetMapping("/crawl_main_page")
	public String getCrawlPage() {
		return "crawl";
	}
}