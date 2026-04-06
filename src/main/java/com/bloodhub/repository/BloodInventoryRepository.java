package com.bloodhub.repository;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.BloodInventory;
import com.bloodhub.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
    Optional<BloodInventory> findByHospitalAndBloodGroup(Hospital hospital, BloodGroup bloodGroup);

    List<BloodInventory> findByHospital(Hospital hospital);

    List<BloodInventory> findByHospital_CityAndBloodGroup(com.bloodhub.entity.City city, BloodGroup bloodGroup);
}
