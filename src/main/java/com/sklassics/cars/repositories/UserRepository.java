package com.sklassics.cars.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.entites.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
}

