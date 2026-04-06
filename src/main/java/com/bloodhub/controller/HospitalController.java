package com.bloodhub.controller;

import com.bloodhub.entity.City;
import com.bloodhub.entity.Hospital;
import com.bloodhub.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /*
     * -------------------------------------------------
     * LIST ALL HOSPITALS
     * -------------------------------------------------
     */
    @GetMapping
    public String listHospitals(@RequestParam(required = false) String query, Model model) {
        List<Hospital> hospitals;
        if (query != null && !query.isBlank()) {
            hospitals = hospitalService.searchHospitals(query);
        } else {
            hospitals = hospitalService.getAllHospitals();
        }
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("query", query);
        model.addAttribute("title", "Hospitals - BloodHub");
        return "hospitals/list";
    }

    /*
     * -------------------------------------------------
     * NEARBY HOSPITALS (CITY + GEOLOCATION SAFE)
     * -------------------------------------------------
     */
    @GetMapping("/nearby")
    public String nearbyHospitals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            Model model) {

        // Convert city string to enum
        City selectedCity = null;
        if (city != null && !city.isBlank()) {
            try {
                // Ensure case-insensitive matching by converting to upper case
                selectedCity = City.valueOf(city.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignore invalid city
            }
        }

        // Fetch hospitals: Use findNearby if lat/lon provided, else filter by city
        List<Hospital> hospitals;
        if (lat != null && lon != null) {
            hospitals = hospitalService.findNearby(lat, lon, selectedCity);
        } else if (selectedCity != null) {
            hospitals = hospitalService.dynamicDiscovery(selectedCity);
        } else {
            hospitals = hospitalService.getActiveHospitals();
        }

        model.addAttribute("selectedCity", selectedCity);
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("title", "Nearby Hospitals");

        return "hospitals/nearby"; // maps to templates/hospitals/nearby.html
    }

    /*
     * -------------------------------------------------
     * VIEW HOSPITAL
     * -------------------------------------------------
     */
    @GetMapping("/view/{id}")
    public String viewHospital(@PathVariable Long id, Model model) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        model.addAttribute("hospital", hospital);
        model.addAttribute("inventory", hospitalService.getInventoryByHospital(hospital));
        model.addAttribute("title", hospital.getName() + " - RED-LINK");
        return "hospitals/view";
    }

    /*
     * -------------------------------------------------
     * CREATE HOSPITAL
     * -------------------------------------------------
     */
    @GetMapping("/create")
    public String createHospitalForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        model.addAttribute("title", "Add Hospital - BloodHub");
        return "hospitals/create";
    }

    @PostMapping("/create")
    public String createHospital(
            @Valid @ModelAttribute("hospital") Hospital hospital,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "hospitals/create";
        }

        hospitalService.createHospital(hospital);
        redirectAttributes.addFlashAttribute("success", "Hospital added successfully!");
        return "redirect:/hospitals";
    }

    /*
     * -------------------------------------------------
     * EDIT HOSPITAL
     * -------------------------------------------------
     */
    @GetMapping("/{id}/edit")
    public String editHospitalForm(@PathVariable Long id, Model model) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        model.addAttribute("hospital", hospital);
        model.addAttribute("title", "Edit Hospital - BloodHub");
        return "hospitals/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateHospital(
            @PathVariable Long id,
            @Valid @ModelAttribute("hospital") Hospital hospital,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "hospitals/edit";
        }

        hospitalService.updateHospital(id, hospital);
        redirectAttributes.addFlashAttribute("success", "Hospital updated successfully!");
        return "redirect:/hospitals/view/" + id;
    }

    /*
     * -------------------------------------------------
     * DELETE HOSPITAL
     * -------------------------------------------------
     */
    @PostMapping("/{id}/delete")
    public String deleteHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hospitalService.deleteHospital(id);
        redirectAttributes.addFlashAttribute("success", "Hospital deleted successfully!");
        return "redirect:/hospitals";
    }

    /*
     * -------------------------------------------------
     * TOGGLE STATUS
     * -------------------------------------------------
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleHospitalStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Hospital hospital = hospitalService.toggleHospitalStatus(id);
        redirectAttributes.addFlashAttribute(
                "success",
                "Hospital " + (hospital.isActive() ? "activated" : "deactivated") + " successfully!");
        return "redirect:/hospitals/view/" + id;
    }
}
