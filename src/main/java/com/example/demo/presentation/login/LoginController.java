package com.example.demo.presentation.login;

import com.example.demo.application.UserService;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userService.findByUsername(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/home";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
