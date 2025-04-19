package com.sklassics.cars.admin.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.dtos.UserDTO;
import com.sklassics.cars.entites.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepository;

    // 1. Get users with 'PENDING' status
    @GetMapping("/pending-documents")
    public ResponseEntity<Map<String, Object>> getUsersWithPendingDocuments() {
        try {
            List<User> pendingUsers = userRepository.findByIsAdminVerifiedDocuments("PENDING");

            if (pendingUsers.isEmpty()) {
                return ResponseEntity.ok(ResponseUtil.notFound("No users found with pending documents"));
            }

            List<UserDTO> userDTOs = pendingUsers.stream().map(user -> {
                UserDTO dto = new UserDTO();
                dto.setEmail(user.getEmail());
                dto.setMobile(user.getMobile());
                dto.setFullName(user.getFullName());
                dto.setAadhaarNumber(user.getAadhaarNumber());
                dto.setAadhaarFilePath(user.getAadhaarFilePath());
                dto.setLicenseFilePath(user.getLicenseFilePath());
                dto.setLocation(user.getLocation());
                dto.setSubmittedAt(user.getSubmittedAt());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ResponseUtil.successWithData("Pending users retrieved successfully", userDTOs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.internalError(e.getMessage()));
        }
    }

    // 2. Approve or Reject documents
    @PostMapping("/verify/{userId}")
    public ResponseEntity<Map<String, Object>> verifyUserDocuments(
            @PathVariable Long userId,
            @RequestParam String status) {

        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.ok(ResponseUtil.notFound(
                        ResponseUtil.ErrorMessages.notFoundWithId("User", userId)));
            }

            if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid status. Use APPROVED or REJECTED."));
            }

            user.setIsAdminVerifiedDocuments(status.toUpperCase());
            userRepository.save(user);

            return ResponseEntity.ok(ResponseUtil.successMessage("User verification status updated to: " + status.toUpperCase()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.internalError(e.getMessage()));
        }
    }
}
