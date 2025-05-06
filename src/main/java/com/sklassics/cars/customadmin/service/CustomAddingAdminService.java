package com.sklassics.cars.customadmin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sklassics.cars.admin.entities.Admin;
import com.sklassics.cars.admin.repositories.AdminRepository;
import com.sklassics.cars.customadmin.entities.CustomAdmin;
import com.sklassics.cars.customadmin.repositories.CustomAdminRepository;
import com.sklassics.cars.services.utility.ResponseUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomAddingAdminService {

    @Autowired
    private CustomAdminRepository customAdminRepository;

    public Map<String, Object> createAdmin(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return ResponseUtil.validationError("Mobile number must not be empty");
        }

        Optional<CustomAdmin> existingAdmin = customAdminRepository.findByMobileNumber(mobileNumber);
        if (existingAdmin.isPresent()) {
            return ResponseUtil.conflict("Admin with this mobile number already exists");
        }

        CustomAdmin admin = new CustomAdmin();
        admin.setMobileNumber(mobileNumber);

        CustomAdmin savedAdmin = customAdminRepository.save(admin);

        return ResponseUtil.successMessage("Admin created successfully");
    }
}
