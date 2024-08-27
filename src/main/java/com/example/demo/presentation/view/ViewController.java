package com.example.demo.presentation.view;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.application.superset.DbDataService;
import com.example.demo.application.user.UserService;
import com.example.demo.infrastructure.jpa.entity.TableListEntitiy;
import com.example.demo.infrastructure.jpa.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {
	
	private final UserService userService;
	private final DbDataService supersetDbService;
	
	@GetMapping("/")
	public String getMainPage(Model model, Authentication authentication) {
		UserEntity user = this.userService.getUserData(authentication.getName());
		model.addAttribute("user", user);
		
		return "index";
	}
	
	@GetMapping("/tableLists")
	public String getTableListPage(Model model, Authentication authentication) {
		UserEntity user = this.userService.getUserData(authentication.getName());
		List<TableListEntitiy> tableData = this.supersetDbService.getTableList();
		
		model.addAttribute("user", user);
		model.addAttribute("dbData", tableData);
		
		return "table_list";
	}
	
	@GetMapping("/configures")
	public String getConfigPage(Model model, Authentication authentication) {
		UserEntity user = this.userService.getUserData(authentication.getName());
		model.addAttribute("user", user);
		
		return "configures";
	}
	
	@GetMapping("/admin")
	public String getAdminPage(Model model, Authentication authentication) {
		UserEntity user = this.userService.getUserData(authentication.getName());
		model.addAttribute("user", user);
		
		return "admin";
	}
	
	@GetMapping("/login_page")
	public String getLoginPage(Model model) {
		return "signin";
	}
	
	@GetMapping("/crawl")
	public String getCrawlPage() {
		return "crawl";
	}
}