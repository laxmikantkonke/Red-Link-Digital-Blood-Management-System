package com.bloodhub.repository;

import com.bloodhub.entity.City;
import com.bloodhub.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByCity(City city);

    List<Hospital> findByIsActiveTrue();

    @Query("SELECT h FROM Hospital h WHERE h.city = :city AND h.isActive = true")
    List<Hospital> findActiveHospitalsByCity(@Param("city") City city);

    @Query("SELECT h FROM Hospital h WHERE " +
            "h.isActive = true AND " +
            "((:city IS NULL) OR (h.city = :city))")
    List<Hospital> findHospitalsByCityOptional(@Param("city") City city);

    @Query("SELECT h FROM Hospital h WHERE h.isActive = true AND " +
            "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Hospital> searchActiveHospitals(@Param("query") String query);

    java.util.Optional<Hospital> findByNameAndCity(String name, City city);

    java.util.Optional<Hospital> findByName(String name);
}
