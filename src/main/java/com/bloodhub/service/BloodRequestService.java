package com.bloodhub.service;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.BloodRequest;
import com.bloodhub.entity.City;
import com.bloodhub.entity.RequestStatus;
import com.bloodhub.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class BloodRequestService {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    public BloodRequest createBloodRequest(BloodRequest bloodRequest) {
        return bloodRequestRepository.save(bloodRequest);
    }

    public BloodRequest updateBloodRequest(BloodRequest bloodRequest) {
        if (!bloodRequestRepository.existsById(bloodRequest.getId())) {
            throw new RuntimeException("Blood request not found with id: " + bloodRequest.getId());
        }
        return bloodRequestRepository.save(bloodRequest);
    }

    public void deleteBloodRequest(Long id) {
        if (!bloodRequestRepository.existsById(id)) {
            throw new RuntimeException("Blood request not found with id: " + id);
        }
        bloodRequestRepository.deleteById(id);
    }

    public Optional<BloodRequest> getBloodRequestById(Long id) {
        return bloodRequestRepository.findById(id);
    }

    public List<BloodRequest> getAllBloodRequests() {
        return bloodRequestRepository.findAll();
    }

    public Page<BloodRequest> getAllBloodRequests(Pageable pageable) {
        return bloodRequestRepository.findAll(pageable);
    }

    public List<BloodRequest> getBloodRequestsByBloodGroup(BloodGroup bloodGroup) {
        return bloodRequestRepository.findByBloodGroup(bloodGroup);
    }

    public List<BloodRequest> getBloodRequestsByCity(City city) {
        return bloodRequestRepository.findByCity(city);
    }

    public List<BloodRequest> getBloodRequestsByBloodGroupAndCity(BloodGroup bloodGroup, City city) {
        return bloodRequestRepository.findByBloodGroupAndCity(bloodGroup, city);
    }

    public List<BloodRequest> getBloodRequestsByStatus(RequestStatus status) {
        return bloodRequestRepository.findByStatus(status);
    }

    public List<BloodRequest> getBloodRequestsByUserId(Long userId) {
        return bloodRequestRepository.findByUserId(userId);
    }

    public Page<BloodRequest> getBloodRequestsByUserId(Long userId, Pageable pageable) {
        return bloodRequestRepository.findByUserId(userId, pageable);
    }

    public List<BloodRequest> getPendingRequestsByBloodGroupAndCity(BloodGroup bloodGroup, City city) {
        return bloodRequestRepository.findPendingRequestsByBloodGroupAndCity(bloodGroup, city);
    }

    public BloodRequest updateBloodRequestStatus(Long id, RequestStatus status) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found with id: " + id));

        bloodRequest.setStatus(status);
        return bloodRequestRepository.save(bloodRequest);
    }

    public List<BloodRequest> getRecentBloodRequests(int limit) {
        return bloodRequestRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<BloodRequest> getUrgentBloodRequests() {
        return bloodRequestRepository.findByUrgencyLevelOrderByCreatedAtDesc(com.bloodhub.entity.UrgencyLevel.CRITICAL);
    }

    public List<BloodRequest> searchBloodRequests(BloodGroup bloodGroup, City city, RequestStatus status, Long userId) {
        return bloodRequestRepository.searchRequests(bloodGroup, city, status, userId);
    }

    public long getCountByStatusAndUserId(RequestStatus status, Long userId) {
        return bloodRequestRepository.countByStatusAndUserId(status, userId);
    }

    public long getCountByUserId(Long userId) {
        return bloodRequestRepository.countByUserId(userId);
    }

    public long getCountByStatus(RequestStatus status) {
        return bloodRequestRepository.countByStatus(status);
    }
}
