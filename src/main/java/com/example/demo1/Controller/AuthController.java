package com.example.demo1.Controller;

import com.example.demo1.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Username cannot be empty");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/register";
        }

        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Password cannot be empty");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/register";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("message", "Passwords do not match");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/register";
        }

        try {
            userService.registerNewUser(username, password);
            redirectAttributes.addFlashAttribute("message", "Registration successful. Please login.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/register";
        }
    }
}