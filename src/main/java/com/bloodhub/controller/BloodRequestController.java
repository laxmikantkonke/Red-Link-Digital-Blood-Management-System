package com.bloodhub.controller;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.BloodRequest;
import com.bloodhub.entity.City;
import com.bloodhub.entity.RequestStatus;
import com.bloodhub.entity.User;
import com.bloodhub.service.BloodRequestService;

import com.bloodhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/blood-requests")
public class BloodRequestController {

    @Autowired
    private BloodRequestService bloodRequestService;

    @Autowired
    private UserService userService;

    /*
     * =========================
     * LIST
     * =========================
     */
    @GetMapping
    public String listBloodRequests(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Pageable pageable = PageRequest.of(page, size);
        Page<BloodRequest> bloodRequests;

        if (isAdmin) {
            bloodRequests = bloodRequestService.getAllBloodRequests(pageable);
            model.addAttribute("pendingCount", bloodRequestService.getCountByStatus(RequestStatus.PENDING));
            model.addAttribute("approvedCount", bloodRequestService.getCountByStatus(RequestStatus.APPROVED));
            model.addAttribute("completedCount", bloodRequestService.getCountByStatus(RequestStatus.COMPLETED));
        } else {
            bloodRequests = bloodRequestService.getBloodRequestsByUserId(currentUser.getId(), pageable);
            model.addAttribute("pendingCount",
                    bloodRequestService.getCountByStatusAndUserId(RequestStatus.PENDING, currentUser.getId()));
            model.addAttribute("approvedCount",
                    bloodRequestService.getCountByStatusAndUserId(RequestStatus.APPROVED, currentUser.getId()));
            model.addAttribute("completedCount",
                    bloodRequestService.getCountByStatusAndUserId(RequestStatus.COMPLETED, currentUser.getId()));
        }

        model.addAttribute("bloodRequests", bloodRequests);
        model.addAttribute("cities", City.values());
        model.addAttribute("title", "Blood Requests - RED-LINK");
        System.out.println("LIST REQUEST handled for: " + email);
        return "blood-requests/list";
    }

    /*
     * =========================
     * CREATE FORM
     * =========================
     */
    @GetMapping("/create")
    public String createBloodRequestForm(Model model) {
        model.addAttribute("bloodRequest", new BloodRequest());
        model.addAttribute("cities", City.values());
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("title", "Post Blood Request - BloodHub");
        return "blood-requests/create";
    }

    /*
     * =========================
     * CREATE SAVE
     * =========================
     */
    @PostMapping("/create")
    public String createBloodRequest(
            @Valid @ModelAttribute("bloodRequest") BloodRequest bloodRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("cities", City.values());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "blood-requests/create";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bloodRequest.setUser(currentUser);
            bloodRequest.setStatus(RequestStatus.PENDING);

            bloodRequestService.createBloodRequest(bloodRequest);
            redirectAttributes.addFlashAttribute("success", "Blood request posted successfully!");
            return "redirect:/blood-requests";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blood-requests/create";
        }
    }

    /*
     * =========================
     * VIEW
     * =========================
     */
    @Autowired
    private com.bloodhub.service.HospitalService hospitalService;

    /*
     * =========================
     * VIEW
     * =========================
     */
    @GetMapping("/{id}")
    public String viewBloodRequest(@PathVariable Long id, Model model) {

        BloodRequest bloodRequest = bloodRequestService.getBloodRequestById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String email = auth.getName();
            User currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!bloodRequest.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only view your own blood requests");
            }
        } else {
            // Admin View: Show availability
            int stock = hospitalService.getTotalStockInCity(bloodRequest.getCity(), bloodRequest.getBloodGroup());
            model.addAttribute("cityStock", stock);
        }

        model.addAttribute("bloodRequest", bloodRequest);
        model.addAttribute("title", "Blood Request Details - BloodHub");
        return "blood-requests/view";
    }

    /*
     * =========================
     * EDIT FORM
     * =========================
     */
    @GetMapping("/{id}/edit")
    public String editBloodRequestForm(@PathVariable Long id, Model model) {

        BloodRequest bloodRequest = bloodRequestService.getBloodRequestById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User currentUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !bloodRequest.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own blood requests");
        }

        model.addAttribute("bloodRequest", bloodRequest);
        model.addAttribute("cities", City.values());
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("title", "Edit Blood Request - BloodHub");

        return "blood-requests/edit";
    }

    /*
     * =========================
     * UPDATE SAVE
     * =========================
     */
    @PostMapping("/{id}/edit")
    public String updateBloodRequest(
            @PathVariable Long id,
            @Valid @ModelAttribute("bloodRequest") BloodRequest bloodRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("cities", City.values());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "blood-requests/edit";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BloodRequest existingRequest = bloodRequestService.getBloodRequestById(id)
                    .orElseThrow(() -> new RuntimeException("Blood request not found"));

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !existingRequest.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only edit your own blood requests");
            }

            existingRequest.setBloodGroup(bloodRequest.getBloodGroup());
            existingRequest.setUrgencyLevel(bloodRequest.getUrgencyLevel());
            existingRequest.setContact(bloodRequest.getContact());
            existingRequest.setCity(bloodRequest.getCity());
            existingRequest.setLocation(bloodRequest.getLocation());
            existingRequest.setDescription(bloodRequest.getDescription());

            bloodRequestService.updateBloodRequest(existingRequest);
            redirectAttributes.addFlashAttribute("success", "Blood request updated successfully!");
            return "redirect:/blood-requests/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blood-requests/" + id + "/edit";
        }
    }

    /*
     * =========================
     * DELETE
     * =========================
     */
    @PostMapping("/{id}/delete")
    public String deleteBloodRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BloodRequest existingRequest = bloodRequestService.getBloodRequestById(id)
                    .orElseThrow(() -> new RuntimeException("Blood request not found"));

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !existingRequest.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only delete your own blood requests");
            }

            bloodRequestService.deleteBloodRequest(id);
            redirectAttributes.addFlashAttribute("success", "Blood request deleted successfully!");
            return "redirect:/blood-requests";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blood-requests/" + id;
        }
    }

    /*
     * =========================
     * SEARCH
     * =========================
     */
    @GetMapping("/search")
    public String searchBloodRequests(
            @RequestParam(required = false) BloodGroup bloodGroup,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) RequestStatus status,
            Model model) {

        System.out.println("Search hitting: bg=" + bloodGroup + ", city=" + city + ", status=" + status);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            System.out.println("Auth is NULL");
            return "redirect:/login";
        }
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long userId = null;
        if (!isAdmin) {
            String email = auth.getName();
            System.out.println("User searching: " + email);
            User currentUser = userService.getUserByEmail(email).orElse(null);
            if (currentUser != null) {
                userId = currentUser.getId();
            }
        }

        List<BloodRequest> bloodRequests = bloodRequestService.searchBloodRequests(bloodGroup, city, status, userId);

        model.addAttribute("cities", City.values());
        model.addAttribute("bloodRequests", bloodRequests);
        model.addAttribute("title", "Search Results - BloodHub");
        return "blood-requests/list";
    }

    /*
     * =========================
     * STATUS UPDATE
     * =========================
     */
    @PostMapping("/{id}/status")
    public String updateRequestStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            BloodRequest bloodRequest = bloodRequestService.getBloodRequestById(id)
                    .orElseThrow(() -> new RuntimeException("Blood request not found"));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                // Users can only cancel
                if (status != RequestStatus.CANCELLED && status != RequestStatus.COMPLETED) {
                    throw new RuntimeException("Users can only Cancel or Complete their requests.");
                }

                String email = auth.getName();
                User currentUser = userService.getUserByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (!bloodRequest.getUser().getId().equals(currentUser.getId())) {
                    throw new RuntimeException("You can only update status of your own blood requests");
                }
            }

            bloodRequestService.updateBloodRequestStatus(id, status);

            // Notifications logic
            if (status == RequestStatus.APPROVED) {
                redirectAttributes.addFlashAttribute("success", "Request APPROVED.");
            } else if (status == RequestStatus.REJECTED) {
                redirectAttributes.addFlashAttribute("info", "Request REJECTED.");
            } else if (status == RequestStatus.NOT_AVAILABLE) {
                redirectAttributes.addFlashAttribute("warning",
                        "Marked NOT AVAILABLE. User notified that blood will be provided after a few hours.");
            } else if (status == RequestStatus.COMPLETED) {
                if (bloodRequest.getStatus() != RequestStatus.APPROVED) {
                    throw new RuntimeException(
                            "Request must be APPROVED by admin before it can be marked as completed.");
                }
                redirectAttributes.addFlashAttribute("success", "Request marked as COMPLETED successfully!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Request status updated successfully!");
            }

        } catch (

        Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/blood-requests/" + id;
    }
}
