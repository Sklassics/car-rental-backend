package com.sklassics.cars.customadmin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.customadmin.entities.CustomUsers;
import com.sklassics.cars.entities.User;

public interface CustomUsersRepository extends JpaRepository<CustomUsers, Long> {
	
    Optional<User> findByMobile(String mobile);
    
    
    Optional<User> findByEmail(String email);
    
    boolean existsByMobile(String mobile);
    
    List<User> findByIsAdminVerifiedDocuments(String isAdminVerifiedDocuments);
    
    Optional<User> findByAddress(String address);
    
    boolean existsByEmail(String email);

}

