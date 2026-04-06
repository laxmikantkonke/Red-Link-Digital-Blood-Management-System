package com.bloodhub.repository;

import com.bloodhub.entity.BloodGroup;
import com.bloodhub.entity.City;
import com.bloodhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    List<User> findByBloodGroup(BloodGroup bloodGroup);

    List<User> findByCity(City city);

    List<User> findByBloodGroupAndCity(BloodGroup bloodGroup, City city);

    List<User> findByEnabled(boolean enabled);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByResetToken(String token);
}
