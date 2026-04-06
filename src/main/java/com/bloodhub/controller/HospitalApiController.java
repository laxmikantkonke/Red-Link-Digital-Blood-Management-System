package com.bloodhub.controller;

import com.bloodhub.entity.Hospital;
import com.bloodhub.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bloodhub.entity.City;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * DYNAMIC ADD HOSPITAL
     * This API allows adding a hospital via JSON payload.
     * Use this for AJAX calls or external integrations.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addHospital(@Valid @RequestBody Hospital hospital) {
        try {
            Hospital savedHospital = hospitalService.createHospital(hospital);
            return ResponseEntity.ok(savedHospital);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding hospital: " + e.getMessage());
        }
    }

    /**
     * GET HOSPITAL DETAILS (JSON)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        return hospitalService.getHospitalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET ALL HOSPITALS (JSON)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    /**
     * UPDATE INVENTORY
     */
    @PostMapping("/{id}/inventory")
    public ResponseEntity<?> updateInventory(@PathVariable Long id,
            @RequestBody InventoryUpdateRequest request) {
        try {
            return ResponseEntity.ok(hospitalService.updateBloodStock(id, request.getBloodGroup(), request.getUnits()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating inventory: " + e.getMessage());
        }
    }

    /**
     * GET INVENTORY
     */
    @GetMapping("/{id}/inventory")
    public ResponseEntity<?> getInventory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hospitalService.getInventory(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching inventory: " + e.getMessage());
        }
    }

    // Inner DTO for update request
    public static class InventoryUpdateRequest {
        private com.bloodhub.entity.BloodGroup bloodGroup;
        private int units;

        public com.bloodhub.entity.BloodGroup getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(com.bloodhub.entity.BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public int getUnits() {
            return units;
        }

        public void setUnits(int units) {
            this.units = units;
        }
    }

    /**
     * SEARCH NEARBY HOSPITALS
     * GET /api/hospitals/nearby?lat=...&lon=...&city=...
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> findNearbyHospitals(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer limit) { // Added limit parameter
        try {
            City selectedCity = null;
            if (city != null && !city.isBlank()) {
                try {
                    selectedCity = City.valueOf(city.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // ignore invalid city
                }
            }
            List<Hospital> hospitals = hospitalService.findNearby(lat, lon, selectedCity);

            // Apply limit if provided
            if (limit != null && limit > 0 && hospitals.size() > limit) {
                hospitals = hospitals.subList(0, limit);
            }

            // Always return a list, even if empty. Frontend handles empty state.
            return ResponseEntity.ok(hospitals);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Error searching nearby hospitals: " + e.getMessage()));
        }
    }
}
