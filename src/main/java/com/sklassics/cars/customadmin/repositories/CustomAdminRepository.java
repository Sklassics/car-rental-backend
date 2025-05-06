package com.sklassics.cars.customadmin.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sklassics.cars.admin.entities.Admin;
import com.sklassics.cars.customadmin.entities.CustomAdmin;

import java.util.Optional;

@Repository
public interface CustomAdminRepository extends JpaRepository<CustomAdmin, Long> {
    Optional<CustomAdmin> findByMobileNumber(String mobileNumber);
}
