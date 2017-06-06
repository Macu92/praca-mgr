package com.pl.pb.predictor.controller.admin.panel;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPanelCotnroller {

	@GetMapping("/admin-panel")
	public String adminPanel(){
		return "admin/admin-panel";
	}
}
