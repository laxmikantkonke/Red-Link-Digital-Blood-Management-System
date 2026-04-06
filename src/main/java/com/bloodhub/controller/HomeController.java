package com.bloodhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "RED-LINK - Blood Management System");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About Us - RED-LINK");
        return "layout/about";
    }

    @GetMapping("/blood-info")
    public String bloodInfo(Model model) {
        model.addAttribute("title", "Blood Information - RED-LINK");
        return "blood-requests/bloodinfo";

    }

    @GetMapping("/achievements")
    public String achievements(Model model) {
        model.addAttribute("title", "Achievements - RED-LINK");
        return "layout/achievements";
    }

}
