package com.bloodhub.controller;

import com.bloodhub.entity.User;
import com.bloodhub.service.UserService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /*
     * =========================
     * "Uttar Pradesh", "Uttarakhand", "West Bengal",
     * "Delhi", "Jammu and Kashmir", "Ladakh",
     * "Puducherry", "Chandigarh",
     * "Andaman and Nicobar Islands",
     * "Dadra and Nagar Haveli and Daman and Diu",
     * "Lakshadweep");
     * }
     * 
     * /*
     * =========================
     * LOGIN
     * =========================
     */
    @GetMapping("/login")
    public String login(Model model,
            @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please check your credentials and try again.");
        }

        model.addAttribute("user", new User());
        model.addAttribute("title", "Login - RED-LINK");
        return "auth/login";
    }

    /*
     * =========================
     * REGISTER FORM
     * =========================
     */
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /*
     * =========================
     * REGISTER SAVE
     * =========================
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        System.out.println("=== REGISTRATION ATTEMPT ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Name: " + user.getName());

        // STEP 1 — generate username if empty
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            if (user.getEmail() != null) {
                user.setUsername(user.getEmail().split("@")[0]);
                System.out.println("Generated username from email: " + user.getUsername());
            }
        }

        // STEP 2 — validate normal fields
        if (result.hasErrors()) {
            System.out.println("Validation errors found:");
            result.getAllErrors().forEach(error -> System.out.println("  - " + error.getDefaultMessage()));
            return "auth/register";
        }

        // ⭐ STEP 3 — check duplicate username/email
        if (userService.existsByUsername(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            result.rejectValue("username", "duplicate", "Username already exists");
        }

        if (userService.existsByEmail(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            result.rejectValue("email", "duplicate", "Email already exists");
        }

        // ⭐ STEP 4 — if duplicate errors exist → return to page
        if (result.hasErrors()) {
            return "auth/register";
        }

        // ⭐ STEP 5 — create user with error handling
        try {
            System.out.println("Attempting to create user...");
            userService.createUser(user);
            System.out.println("User created successfully!");

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful! Please login.");

            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("ERROR during user creation:");
            e.printStackTrace();

            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    /*
     * =========================
     * FORGOT PASSWORD
     * =========================
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password - RED-LINK");
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        userService.createPasswordResetTokenForUser(email);

        redirectAttributes.addFlashAttribute(
                "success",
                "If an account with that email exists, we've sent you a password reset link.");

        return "redirect:/login";
    }

    /*
     * =========================
     * RESET PASSWORD
     * =========================
     */
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {

        model.addAttribute("token", token);
        model.addAttribute("title", "Reset Password - RED-LINK");

        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        User user = userService.getUserByResetToken(token).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        userService.updatePassword(user, password);

        redirectAttributes.addFlashAttribute(
                "success",
                "Password reset successful! Please login with your new password.");

        return "redirect:/login";
    }
}
