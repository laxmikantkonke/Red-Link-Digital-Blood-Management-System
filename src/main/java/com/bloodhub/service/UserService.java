package com.bloodhub.service;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.City;
import com.bloodhub.entity.User;
import com.bloodhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    // ================= SPRING SECURITY LOGIN =================
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String input)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Dynamically fetch roles from the user entity
        java.util.List<String> roleList = user.getRoles().stream()
                .map(role -> role.getName().name().replace("ROLE_", ""))
                .collect(java.util.stream.Collectors.toList());

        // Safety fallback: if no roles exist in DB, treat as USER for now
        if (roleList.isEmpty()) {
            roleList.add("USER");
        }

        String[] roles = roleList.toArray(new String[0]);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // email as principal (expected by controllers)
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .roles(roles)
                .build();
    }

    @Autowired
    private com.bloodhub.repository.RoleRepository roleRepository;

    // ================= REGISTRATION =================
    public void createUser(User user) {
        System.out.println("=== UserService.createUser() START ===");
        System.out.println("Creating user: " + user.getEmail());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Name: " + user.getName());

        // Encode password
        System.out.println("Encoding password...");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Password encoded successfully");

        user.setEnabled(true);

        // Assign default ROLE_USER
        System.out.println("Assigning ROLE_USER...");
        com.bloodhub.entity.Role userRole = roleRepository.findByName(com.bloodhub.entity.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        java.util.Set<com.bloodhub.entity.Role> roles = new java.util.HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        System.out.println("Role assigned: ROLE_USER");

        // Save user
        System.out.println("Saving user to database...");
        userRepository.save(user);
        System.out.println("=== UserService.createUser() SUCCESS ===");
        System.out.println("User saved with ID: " + user.getId());
    }

    // ================= CHECK EXISTENCE =================
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ================= FETCH USER =================
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsersPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void updateUser(User updatedUser) {
        System.out.println("---------- UserService.updateUser() START ----------");
        if (updatedUser == null) {
            System.out.println("ERROR: updatedUser is null");
            return;
        }

        // Try to find by ID first, then Email
        Optional<User> existingUserOpt = Optional.empty();
        if (updatedUser.getId() != null) {
            System.out.println("Searching user by ID: " + updatedUser.getId());
            existingUserOpt = userRepository.findById(updatedUser.getId());
        }

        if (existingUserOpt.isEmpty() && updatedUser.getEmail() != null) {
            System.out.println("Searching user by Email: " + updatedUser.getEmail());
            existingUserOpt = userRepository.findByEmail(updatedUser.getEmail());
        }

        existingUserOpt.ifPresentOrElse(user -> {
            System.out.println("User FOUND. Current stored name: " + user.getName());
            System.out.println("Applying updates from form:");

            System.out.println("  Name: " + user.getName() + " -> " + updatedUser.getName());
            user.setName(updatedUser.getName());

            System.out.println("  Contact: " + user.getContact() + " -> " + updatedUser.getContact());
            user.setContact(updatedUser.getContact());

            System.out.println("  BloodGroup: " + user.getBloodGroup() + " -> " + updatedUser.getBloodGroup());
            user.setBloodGroup(updatedUser.getBloodGroup());

            System.out.println("  City: " + user.getCity() + " -> " + updatedUser.getCity());
            user.setCity(updatedUser.getCity());

            System.out.println("  State: " + user.getState() + " -> " + updatedUser.getState());
            user.setState(updatedUser.getState());

            System.out.println("  Address: " + user.getAddress() + " -> " + updatedUser.getAddress());
            user.setAddress(updatedUser.getAddress());

            System.out.println("  Gender: " + user.getGender() + " -> " + updatedUser.getGender());
            user.setGender(updatedUser.getGender());

            System.out.println("  Pincode: " + user.getPincode() + " -> " + updatedUser.getPincode());
            user.setPincode(updatedUser.getPincode());

            System.out.println("  Enabled: " + user.isEnabled() + " -> " + updatedUser.isEnabled());
            user.setEnabled(updatedUser.isEnabled());

            System.out.println("Saving to repository...");
            userRepository.save(user);
            userRepository.flush();
            System.out.println("SAVE AND FLUSH COMPLETED");
        }, () -> {
            System.out.println("ERROR: No user found to update!");
        });
        System.out.println("---------- UserService.updateUser() END ----------");
    }

    // ================= DONOR SEARCH =================
    public java.util.List<User> getDonorsByBloodGroup(BloodGroup bloodGroup) {
        return userRepository.findByBloodGroup(bloodGroup);
    }

    public java.util.List<User> getDonorsByCity(City city) {
        return userRepository.findByCity(city);
    }

    public java.util.List<User> getDonorsByBloodGroupAndCity(BloodGroup bloodGroup, City city) {
        return userRepository.findByBloodGroupAndCity(bloodGroup, city);
    }

    public void deleteUser(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    // ================= PASSWORD RESET =================
    public void createPasswordResetTokenForUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            String resetUrl = "http://localhost:8080/reset-password?token=" + token;
            String body = "To reset your password, click the link below:\n" + resetUrl;
            mailService.sendEmail(user.getEmail(), "Password Reset Request", body);
        });
    }

    public Optional<User> getUserByResetToken(String token) {
        return userRepository.findByResetToken(token)
                .filter(user -> user.getResetTokenExpiry().isAfter(LocalDateTime.now()));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
