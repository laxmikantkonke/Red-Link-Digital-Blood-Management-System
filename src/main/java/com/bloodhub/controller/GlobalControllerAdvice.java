package com.bloodhub.controller;

import com.bloodhub.entity.City;
import com.bloodhub.entity.User;
import com.bloodhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("cities")
    public City[] cities() {
        return City.values();
    }

    @ModelAttribute("states")
    public com.bloodhub.entity.State[] states() {
        return new com.bloodhub.entity.State[] {
                com.bloodhub.entity.State.MAHARASHTRA,
                com.bloodhub.entity.State.TELANGANA
        };
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return userService.getUserByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard";
    }
}
