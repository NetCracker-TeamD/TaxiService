package com.teamd.taxi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

	@RequestMapping("/order")
	public String greeting(
			@RequestParam(value = "name", required = false, defaultValue = "Anonymous user") String name,
			Model model) {
		model.addAttribute("name", name);
		return "order";
	}

}
