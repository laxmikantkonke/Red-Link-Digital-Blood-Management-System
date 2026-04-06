package com.bloodhub.repository;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.BloodRequest;
import com.bloodhub.entity.City;
import com.bloodhub.entity.RequestStatus;
import com.bloodhub.entity.UrgencyLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

        List<BloodRequest> findByBloodGroup(BloodGroup bloodGroup);

        List<BloodRequest> findByCity(City city);

        List<BloodRequest> findByBloodGroupAndCity(BloodGroup bloodGroup, City city);

        List<BloodRequest> findByStatus(RequestStatus status);

        List<BloodRequest> findByUserId(Long userId);

        org.springframework.data.domain.Page<BloodRequest> findByUserId(Long userId,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT br FROM BloodRequest br WHERE br.bloodGroup = :bloodGroup AND br.city = :city AND br.status = 'PENDING'")
        List<BloodRequest> findPendingRequestsByBloodGroupAndCity(@Param("bloodGroup") BloodGroup bloodGroup,
                        @Param("city") City city);

        // Page<BloodRequest> findAll(Pageable pageable); // Inherited from
        // PagingAndSortingRepository

        List<BloodRequest> findTop10ByOrderByCreatedAtDesc();

        List<BloodRequest> findByUrgencyLevelOrderByCreatedAtDesc(UrgencyLevel urgencyLevel);

        List<BloodRequest> findByStatusAndUrgencyLevelOrderByCreatedAtDesc(RequestStatus status,
                        UrgencyLevel urgencyLevel);

        @Query("SELECT br FROM BloodRequest br WHERE city = :city AND br.status = 'PENDING' ORDER BY br.urgencyLevel DESC, br.createdAt DESC")
        List<BloodRequest> findPendingRequestsByCityOrderByUrgencyAndDate(@Param("city") City city);

        @Query("SELECT br FROM BloodRequest br WHERE " +
                        "(:bloodGroup IS NULL OR br.bloodGroup = :bloodGroup) AND " +
                        "(:city IS NULL OR br.city = :city) AND " +
                        "(:status IS NULL OR br.status = :status) AND " +
                        "(:userId IS NULL OR br.user.id = :userId) " +
                        "ORDER BY br.createdAt DESC")
        List<BloodRequest> searchRequests(
                        @Param("bloodGroup") BloodGroup bloodGroup,
                        @Param("city") City city,
                        @Param("status") RequestStatus status,
                        @Param("userId") Long userId);

        long countByStatusAndUserId(RequestStatus status, Long userId);

        long countByUserId(Long userId);

        long countByStatus(RequestStatus status);
}
