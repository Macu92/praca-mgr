package com.pl.pb.predictor.controller.user.panel;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserPanelController {

	@GetMapping("/user-panel")
	public String userPanel(){
		return "user-panel";
	}
}
