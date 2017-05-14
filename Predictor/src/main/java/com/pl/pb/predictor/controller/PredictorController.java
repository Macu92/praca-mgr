package com.pl.pb.predictor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pl.pb.predictor.controller.forms.LoginForm;

@Controller
public class PredictorController {

	
	@GetMapping("/")
	public String index() {
        return "index";
    }
	
	@PostMapping("/login")
	public String login(@ModelAttribute LoginForm loginForm) {
		System.out.println(loginForm.getUsername());
        return "redirect:/admin-panel";
    }
}
