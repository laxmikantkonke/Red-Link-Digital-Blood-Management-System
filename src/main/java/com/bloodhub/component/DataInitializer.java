package com.bloodhub.component;

import com.bloodhub.entity.ERole;
import com.bloodhub.entity.Role;
import com.bloodhub.entity.User;
import com.bloodhub.entity.State;
import com.bloodhub.entity.City;
import com.bloodhub.repository.RoleRepository;
import com.bloodhub.repository.UserRepository;
import com.bloodhub.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        // Run migration FIRST to avoid any load-time Enum constant issues
        migrateData();
        initializeRoles();
        initializeAdminUser();
        initializeTestUser();
        initializeHospitalsAndInventory();
    }

    private void migrateData() {
        try {
            // Update users and blood_requests to a valid city before anything else tries to
            // load them
            String validCities = "'MUMBAI', 'PUNE', 'NAGPUR', 'THANE', 'NASHIK', 'SOLAPUR', 'AMRAVATI', 'KOLHAPUR', 'SANGLI', 'JALGAON', 'AKOLA', 'NANDED', 'LATUR', 'SATARA', 'HYDERABAD', 'WARANGAL'";
            entityManager
                    .createNativeQuery(
                            "UPDATE users SET city = 'MUMBAI' WHERE city NOT IN (" + validCities + ") OR city IS NULL")
                    .executeUpdate();
            entityManager.createNativeQuery(
                    "UPDATE blood_requests SET city = 'MUMBAI' WHERE city NOT IN (" + validCities + ") OR city IS NULL")
                    .executeUpdate();
            System.out.println("Data migration completed successfully.");
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
        }
    }

    private void initializeHospitalsAndInventory() {
        System.out.println("Initializing real-world hospitals from OSM API...");
        try {
            hospitalService.initializeAllCitiesWithRealHospitals();
            System.out.println("Hospitals and Inventory initialized for all cities.");
        } catch (Exception e) {
            System.err.println("Failed to initialize hospitals: " + e.getMessage());
        }
    }

    private void initializeRoles() {
        for (ERole roleName : ERole.values()) {
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                Role newRole = new Role();
                newRole.setName(roleName);
                roleRepository.save(newRole);
                System.out.println("Created role: " + roleName);
            }
        }
    }

    private void initializeAdminUser() {
        User existingAdmin = userRepository.findByEmail("admin@bloodhub.com").orElse(null);
        if (existingAdmin != null) {
            System.out.println("Admin already exists.");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setName("Administrator");
        admin.setEmail("admin@bloodhub.com");
        admin.setPassword(passwordEncoder.encode("admin@123"));
        admin.setContact("1234678945");
        admin.setAddress("BloodHub HQ, Pune");
        admin.setPincode("411001");
        admin.setState(State.MAHARASHTRA);
        admin.setEnabled(true);

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        userRepository.save(admin);
        System.out.println("Created admin user: admin@bloodhub.com / admin@123");
    }

    private void initializeTestUser() {
        User existingTest = userRepository.findByEmail("test@bloodhub.com").orElse(null);
        if (existingTest != null) {
            System.out.println("Test user already exists.");
            return;
        }

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setEmail("test@bloodhub.com");
        testUser.setPassword(passwordEncoder.encode("test123"));
        testUser.setContact("9876543210");
        testUser.setAddress("Test Address, Mumbai");
        testUser.setPincode("400001");
        testUser.setGender(com.bloodhub.entity.Gender.MALE);
        testUser.setBloodGroup(com.bloodhub.entity.BloodGroup.O_POSITIVE);
        testUser.setCity(City.MUMBAI);
        testUser.setState(State.MAHARASHTRA);
        testUser.setEnabled(true);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);

        userRepository.save(testUser);
        System.out.println("Created test user: test@bloodhub.com / test123");
    }

}
