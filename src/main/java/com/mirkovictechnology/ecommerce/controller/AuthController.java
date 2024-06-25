package com.mirkovictechnology.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mirkovictechnology.ecommerce.model.RoleName;
import com.mirkovictechnology.ecommerce.model.User;
import com.mirkovictechnology.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService userService;
	
	@GetMapping("/register")
	public String showRegisterPage(final Model model) {
		model.addAttribute("user", new User());
		return "register";
	}
	
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}
	
	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") User user) {
		userService.registerNewUser(user);
		return "redirect:/login";
	}
	
	@GetMapping("/process")
	public String process(final HttpServletRequest request) {
		if (request.isUserInRole(RoleName.ADMIN.name())) {
			return "redirect:admin/admin-panel";
		}
		else if (request.isUserInRole(RoleName.USER.name())) {
			return "redirect:shop/shop-panel";
		}
		else {
			return "redirect:/login";
		}
	}
}