package com.sklassics.cars.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    
    List<User> findByIsAdminVerifiedDocuments(String isAdminVerifiedDocuments);
    
    Optional<User> findByAddress(String address);

}

