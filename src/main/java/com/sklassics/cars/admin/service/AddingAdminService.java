package com.sklassics.cars.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sklassics.cars.admin.entities.Admin;
import com.sklassics.cars.admin.repositories.AdminRepository;
import com.sklassics.cars.services.utility.ResponseUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AddingAdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Map<String, Object> createAdmin(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return ResponseUtil.validationError("Mobile number must not be empty");
        }

        Optional<Admin> existingAdmin = adminRepository.findByMobileNumber(mobileNumber);
        if (existingAdmin.isPresent()) {
            return ResponseUtil.conflict("Admin with this mobile number already exists");
        }

        Admin admin = new Admin();
        admin.setMobileNumber(mobileNumber);

        Admin savedAdmin = adminRepository.save(admin);

        return ResponseUtil.successMessage("Admin created successfully");
    }
}
