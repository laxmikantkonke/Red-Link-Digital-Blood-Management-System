package com.bloodhub.service;

import com.bloodhub.entity.City;
import com.bloodhub.entity.Hospital;
import com.bloodhub.entity.BloodInventory;
import com.bloodhub.repository.BloodInventoryRepository;
import com.bloodhub.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    private GeocodingService geocodingService;

    // Get all hospitals
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    // Get paginated hospitals
    public org.springframework.data.domain.Page<Hospital> getHospitalsPage(
            org.springframework.data.domain.Pageable pageable) {
        return hospitalRepository.findAll(pageable);
    }

    // Get hospital by ID
    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    // Create new hospital
    public Hospital createHospital(Hospital hospital) {
        if (hospital.getLatitude() == null || hospital.getLongitude() == null) {
            enrichCoordinates(hospital);
        }
        return hospitalRepository.save(hospital);
    }

    public List<BloodInventory> getInventoryByHospital(Hospital hospital) {
        return bloodInventoryRepository.findByHospital(hospital);
    }

    // Update hospital
    public Hospital updateHospital(Long id, Hospital hospitalDetails) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        hospital.setName(hospitalDetails.getName());
        hospital.setAddress(hospitalDetails.getAddress());
        hospital.setCity(hospitalDetails.getCity());
        hospital.setContact(hospitalDetails.getContact());
        hospital.setEmail(hospitalDetails.getEmail());
        hospital.setWebsite(hospitalDetails.getWebsite());
        // Update lat/lon only if provided or if address changed and lat/lon is missing
        if (hospitalDetails.getLatitude() != null && hospitalDetails.getLongitude() != null) {
            hospital.setLatitude(hospitalDetails.getLatitude());
            hospital.setLongitude(hospitalDetails.getLongitude());
        } else {
            // If manual coords are NOT provided, verify if we should re-fetch
            // Simpler approach: If provided coords are null, try to fetch from new address
            hospital.setLatitude(null);
            hospital.setLongitude(null);
            enrichCoordinates(hospital); // will use the UPDATED address
        }

        return hospitalRepository.save(hospital);
    }

    private void enrichCoordinates(Hospital hospital) {
        String fullAddress = hospital.getName() + ", " + hospital.getAddress();
        if (hospital.getCity() != null) {
            fullAddress += ", " + hospital.getCity().getDisplayName();
        }

        Optional<double[]> coords = geocodingService.getCoordinates(fullAddress);
        if (coords.isPresent()) {
            hospital.setLatitude(coords.get()[0]);
            hospital.setLongitude(coords.get()[1]);
        }
    }

    // Delete hospital
    public void deleteHospital(Long id) {
        hospitalRepository.deleteById(id);
    }

    // Hospitals by City
    public List<Hospital> getHospitalsByCity(City city) {
        return hospitalRepository.findByCity(city);
    }

    // Active hospitals
    public List<Hospital> getActiveHospitals() {
        return hospitalRepository.findByIsActiveTrue();
    }

    public List<Hospital> searchHospitals(String query) {
        if (query == null || query.isBlank()) {
            return getActiveHospitals();
        }
        return hospitalRepository.searchActiveHospitals(query.trim());
    }

    public List<Hospital> getActiveHospitalsByCity(City city) {
        return hospitalRepository.findActiveHospitalsByCity(city);
    }

    public Hospital toggleHospitalStatus(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        hospital.setActive(!hospital.isActive());
        return hospitalRepository.save(hospital);
    }

    /**
     * DYNAMIC DISCOVERY
     * Checks if hospitals exist for a city. If not, fetches real ones from OSM and
     * saves them.
     */
    public List<Hospital> dynamicDiscovery(City city) {
        List<Hospital> existing = hospitalRepository.findActiveHospitalsByCity(city);

        // Define per-city limits
        int targetCount = (city == City.MUMBAI || city == City.PUNE) ? 20 : 10;

        if (existing.size() < targetCount) {
            // Fetch real hospitals for this city from OpenStreetMap
            List<Hospital> discovered = geocodingService.searchHospitalsInCity(city.getDisplayName(), city);
            if (!discovered.isEmpty()) {
                int count = existing.size();
                for (Hospital h : discovered) {
                    if (count >= targetCount)
                        break;

                    // Avoid duplicates by name and city
                    if (hospitalRepository.findByNameAndCity(h.getName(), city).isEmpty()) {
                        hospitalRepository.save(h);
                        // Initialize inventory for the new hospital
                        initializeDefaultInventory(h);
                        count++;
                    }
                }
                // Refresh list from DB
                return hospitalRepository.findActiveHospitalsByCity(city);
            }
        }
        return existing;
    }

    private void initializeDefaultInventory(Hospital hospital) {
        if (bloodInventoryRepository.findByHospital(hospital).isEmpty()) {
            for (com.bloodhub.entity.BloodGroup bg : com.bloodhub.entity.BloodGroup.values()) {
                int quantity = (int) (Math.random() * 20); // 0 to 19 units
                BloodInventory inventory = new BloodInventory(hospital, bg, quantity);
                bloodInventoryRepository.save(inventory);
            }
        }
    }

    /**
     * Loops through all defined cities and ensures each has real hospitals.
     */
    public void initializeAllCitiesWithRealHospitals() {
        for (City city : City.values()) {
            System.out.println("Ensuring real hospitals for city: " + city.getDisplayName());
            dynamicDiscovery(city);
        }
    }

    public List<Hospital> findNearby(Double lat, Double lon, City city) {
        // For city-based search (no coordinates), return all active hospitals for that
        // city
        if ((lat == null || lon == null) && city != null) {
            return hospitalRepository.findActiveHospitalsByCity(city);
        }

        // If no city and no coordinates, return empty
        if (lat == null || lon == null) {
            return List.of();
        }

        // For coordinate-based search with optional city filter
        double radiusKm = 40.0;
        List<Hospital> hospitals;

        if (city != null) {
            hospitals = hospitalRepository.findActiveHospitalsByCity(city);
        } else {
            hospitals = hospitalRepository.findByIsActiveTrue();
        }

        List<Hospital> nearby = new ArrayList<>();
        for (Hospital h : hospitals) {
            if (h.getLatitude() != null && h.getLongitude() != null) {
                double distance = distanceInKm(lat, lon, h.getLatitude(), h.getLongitude());
                if (distance <= radiusKm) {
                    nearby.add(h);
                }
            }
        }

        // Fallback to all city hospitals if none within radius
        if (nearby.isEmpty() && city != null) {
            return hospitalRepository.findActiveHospitalsByCity(city);
        }

        return nearby;
    }

    // Haversine — distance in km
    private double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Link inventory to hospital
    public BloodInventory updateBloodStock(Long hospitalId, com.bloodhub.entity.BloodGroup bloodGroup, int units) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Optional<BloodInventory> existingInventory = bloodInventoryRepository.findByHospitalAndBloodGroup(hospital,
                bloodGroup);

        BloodInventory inventory;
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setUnits(units);
        } else {
            inventory = new BloodInventory(hospital, bloodGroup, units);
        }
        inventory.setLastUpdated(java.time.LocalDateTime.now());

        return bloodInventoryRepository.save(inventory);
    }

    public List<BloodInventory> getInventory(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        return bloodInventoryRepository.findByHospital(hospital);
    }

    public Integer getTotalStockInCity(City city, com.bloodhub.entity.BloodGroup bloodGroup) {
        List<BloodInventory> inventories = bloodInventoryRepository.findByHospital_CityAndBloodGroup(city, bloodGroup);
        return inventories.stream()
                .mapToInt(BloodInventory::getUnits)
                .sum();
    }
}
