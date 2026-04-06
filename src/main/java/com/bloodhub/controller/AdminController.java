package com.bloodhub.controller;

import com.bloodhub.entity.City;
import com.bloodhub.service.BloodRequestService;
import com.bloodhub.service.HospitalService;
import com.bloodhub.service.UserService;
import com.bloodhub.entity.Hospital;
import com.bloodhub.entity.User;

import com.bloodhub.entity.BloodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BloodRequestService bloodRequestService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalRequests", bloodRequestService.getAllBloodRequests().size());
        model.addAttribute("totalHospitals", hospitalService.getAllHospitals().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("recentRequests", bloodRequestService.getAllBloodRequests());

        model.addAttribute("title", "Admin Dashboard - BloodHub");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getUsersPage(page, size);

        model.addAttribute("users", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());

        model.addAttribute("title", "Manage Users - Admin Panel");
        return "admin/users";
    }

    @GetMapping("/blood-requests")
    public String listBloodRequests(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BloodRequest> requestPage = bloodRequestService.getAllBloodRequests(PageRequest.of(page, size));

        model.addAttribute("requests", requestPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("totalItems", requestPage.getTotalElements());

        model.addAttribute("title", "Manage Blood Requests - Admin Panel");
        return "admin/blood-requests";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users?deleted";
    }

    @PostMapping("/blood-requests/{id}/delete")
    public String deleteRequests(@PathVariable Long id) {
        bloodRequestService.deleteBloodRequest(id);
        return "redirect:/admin/dashboard?deleted";
    }

    @GetMapping("/hospitals/add")
    public String addHospitalForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        model.addAttribute("cities", City.values());
        model.addAttribute("title", "Add Hospital - Admin Panel");
        return "admin/add-hospital";
    }

    @PostMapping("/hospitals/add")
    public String addHospital(
            @Valid @ModelAttribute("hospital") Hospital hospital,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("cities", City.values());
            model.addAttribute("title", "Add Hospital - Admin Panel");
            return "admin/add-hospital";
        }

        hospitalService.createHospital(hospital);
        redirectAttributes.addFlashAttribute("success", "Hospital added successfully!");
        return "redirect:/admin/dashboard";
    }
}
