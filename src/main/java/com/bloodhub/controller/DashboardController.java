package com.bloodhub.controller;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.City;
import com.bloodhub.entity.User;
import com.bloodhub.service.BloodRequestService;
import com.bloodhub.service.HospitalService;
import com.bloodhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private BloodRequestService bloodRequestService;

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Redirect admin to admin dashboard
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        // Get current user's email
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email).orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get user-specific blood requests
        java.util.List<com.bloodhub.entity.BloodRequest> userRequests = bloodRequestService
                .getBloodRequestsByUserId(currentUser.getId());

        model.addAttribute("recentRequests", userRequests);

        // Get nearby hospitals
        model.addAttribute("nearbyHospitals", hospitalService.getActiveHospitals());

        // Get statistics - user-specific request count
        model.addAttribute("totalRequests", userRequests.size());
        model.addAttribute("pendingCount",
                bloodRequestService.getCountByStatusAndUserId(com.bloodhub.entity.RequestStatus.PENDING,
                        currentUser.getId()));
        model.addAttribute("approvedCount",
                bloodRequestService.getCountByStatusAndUserId(com.bloodhub.entity.RequestStatus.APPROVED,
                        currentUser.getId()));
        model.addAttribute("totalHospitals", hospitalService.getAllHospitals().size());

        model.addAttribute("title", "Dashboard - RED-LINK");
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "Profile - BloodHub");
        return "profile";
    }

    @GetMapping("/profile/details/{id}")
    public String profileDetails(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("profileUser", user);
        model.addAttribute("title", user.getName() + "'s Profile - BloodHub");
        return "profile-details";
    }

    @GetMapping("/search-donor")
    public String searchDonor(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            Model model) {

        BloodGroup bgEnum = null;
        City cityEnum = null;

        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            try {
                bgEnum = BloodGroup.valueOf(bloodGroup.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid blood group
            }
        }

        if (city != null && !city.isEmpty()) {
            try {
                cityEnum = City.valueOf(city.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid city
            }
        }

        if (bgEnum != null || cityEnum != null) {
            List<User> donors;
            if (bgEnum != null && cityEnum != null) {
                donors = userService.getDonorsByBloodGroupAndCity(bgEnum, cityEnum);
            } else if (bgEnum != null) {
                donors = userService.getDonorsByBloodGroup(bgEnum);
            } else {
                donors = userService.getDonorsByCity(cityEnum);
            }
            model.addAttribute("donors", donors);
        }

        // Create safe lists for filters
        List<Map<String, String>> bloodGroupList = new ArrayList<>();
        for (BloodGroup bg : BloodGroup.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", bg.name());
            map.put("displayName", bg.getDisplayName());
            bloodGroupList.add(map);
        }

        List<Map<String, String>> cityList = new ArrayList<>();
        for (City c : City.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", c.name());
            map.put("displayName", c.getDisplayName());
            cityList.add(map);
        }

        model.addAttribute("bloodGroups", bloodGroupList);
        model.addAttribute("cities", cityList);
        model.addAttribute("title", "Search Donor - BloodHub");
        return "search-donor";
    }

    @GetMapping("/profile/update")
    public String editProfile(Model model) {
        model.addAttribute("title", "Edit Profile - BloodHub");
        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(User formUser) {
        if (formUser != null) {
            userService.updateUser(formUser);
        }
        return "redirect:/profile?success";
    }
}
