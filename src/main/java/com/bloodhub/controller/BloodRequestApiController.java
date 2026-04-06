package com.bloodhub.controller;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.BloodRequest;
import com.bloodhub.entity.City;
import com.bloodhub.entity.RequestStatus;
import com.bloodhub.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
public class BloodRequestApiController {

    @Autowired
    private BloodRequestService bloodRequestService;

    /**
     * GET ALL BLOOD REQUESTS (JSON)
     */
    @GetMapping("/all")
    public ResponseEntity<List<BloodRequest>> getAllRequests() {
        return ResponseEntity.ok(bloodRequestService.getAllBloodRequests());
    }

    /**
     * GET BLOOD REQUEST BY ID (JSON)
     */
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequest> getRequestById(@PathVariable Long id) {
        return bloodRequestService.getBloodRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * SEARCH BLOOD REQUESTS (JSON)
     * GET /api/blood-requests/search?bloodGroup=...&city=...&status=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<BloodRequest>> searchRequests(
            @RequestParam(required = false) BloodGroup bloodGroup,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) RequestStatus status) {

        List<BloodRequest> requests = bloodRequestService.searchBloodRequests(bloodGroup, city, status, null);
        return ResponseEntity.ok(requests);
    }
}
