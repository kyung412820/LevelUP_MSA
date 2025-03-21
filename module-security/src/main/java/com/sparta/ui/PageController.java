package com.sparta.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

	@GetMapping("/v2/signin")
	public String signInPage() {
		return "signin";
	}

	@GetMapping("/v2/signup")
	public String signUpUserPage() {
		return "signup";
	}

	@GetMapping("/v2/oauth2signup")
	public String oAuth2SignUpUserPage(Model model, HttpServletRequest request) {

		model.addAttribute("email", request.getAttribute("email"));
		model.addAttribute("nickName", request.getAttribute("nickName"));
		model.addAttribute("phoneNummber", request.getAttribute("phoneNumber"));

		return "oauth2signup";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	// 메인 페이지
	@GetMapping("/")
	public String mainPage() {
		return "main";
	}

	// 메인 페이지
	@GetMapping("/chat-main")
	public String chatMainPage() {
		return "chatMain";
	}

	@GetMapping("/product-detail")
	public String productDetail(@RequestParam("productId") Long productId, Model model) {
		model.addAttribute("productId", productId);
		return "productDetail";
	}

	@GetMapping("/resetPassword")
	public String resetPassword() {
		return "resetPassword";
	}

	@GetMapping("/resetPasswordConfirm")
	public String resetPasswordConfirm() {
		return "resetPasswordConfirm";
	}
}
